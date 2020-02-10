/*
 * This file is part of the WannaGo distribution (https://github.com/wannago).
 * Copyright (c) [2019] - [2020].
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, version 3.
 *
 * This program is distributed in the hope that it will be useful, but
 * WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program. If not, see <http://www.gnu.org/licenses/>.
 */


package org.wannagoframework.i18n.config;

import com.hazelcast.config.Config;
import com.hazelcast.config.DiscoveryConfig;
import com.hazelcast.config.DiscoveryStrategyConfig;
import com.hazelcast.config.EvictionPolicy;
import com.hazelcast.config.GroupConfig;
import com.hazelcast.config.ManagementCenterConfig;
import com.hazelcast.config.MapConfig;
import com.hazelcast.config.MaxSizeConfig;
import com.hazelcast.config.MemberAddressProviderConfig;
import com.hazelcast.core.Hazelcast;
import com.hazelcast.core.HazelcastInstance;
import org.bitsofinfo.hazelcast.discovery.docker.swarm.SwarmMemberAddressProvider;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.web.ServerProperties;
import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.cloud.client.ServiceInstance;
import org.springframework.cloud.client.discovery.DiscoveryClient;
import org.springframework.cloud.client.serviceregistry.Registration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;
import org.springframework.core.env.Profiles;
import org.wannagoframework.commons.utils.SpringProfileConstants;

@Configuration
@EnableCaching
public class CacheConfiguration implements DisposableBean {

  private final Logger log = LoggerFactory.getLogger(CacheConfiguration.class);

  private final Environment env;

  private final ServerProperties serverProperties;

  private final DiscoveryClient discoveryClient;

  private Registration registration;

  public CacheConfiguration(Environment env, ServerProperties serverProperties,
      DiscoveryClient discoveryClient) {
    this.env = env;
    this.serverProperties = serverProperties;
    this.discoveryClient = discoveryClient;
  }

  @Autowired(required = false)
  public void setRegistration(Registration registration) {
    this.registration = registration;
  }

  @Override
  public void destroy() throws Exception {
    log.info("Closing Cache Manager");
    Hazelcast.shutdownAll();
  }

  @Bean
  public CacheManager cacheManager(HazelcastInstance hazelcastInstance) {
    log.debug("Starting HazelcastCacheManager");
    return new com.hazelcast.spring.cache.HazelcastCacheManager(hazelcastInstance);
  }

  @Bean
  public HazelcastInstance hazelcastInstance(AppProperties appProperties) {
    log.debug("Configuring Hazelcast");
    HazelcastInstance hazelCastInstance = Hazelcast
        .getHazelcastInstanceByName("doYouWannaPlay-i18n");
    if (hazelCastInstance != null) {
      log.debug("Hazelcast already initialized");
      return hazelCastInstance;
    }
    Config config = new Config();
    config.setInstanceName("doYouWannaPlay-i18n");

    GroupConfig groupConfig = new GroupConfig();
    groupConfig.setName("doYouWannaPlay-i18n");
    config.setGroupConfig(groupConfig);

    config.getNetworkConfig().getJoin().getMulticastConfig().setEnabled(false);
    if (this.registration == null) {
      log.warn("No discovery service is set up, Hazelcast cannot create a cluster.");
    } else {
      // The serviceId is by default the application's name,
      // see the "spring.application.name" standard Spring property
      String serviceId = registration.getServiceId();
      log.debug("Configuring Hazelcast clustering for instanceId: {}", serviceId);
      // In development, everything goes through 127.0.0.1, with a different port
      if (env
          .acceptsProfiles(Profiles.of(SpringProfileConstants.SPRING_PROFILE_DEVELOPMENT_LOCAL))) {
        log.debug("Application is running with the \"devlocal\" profile, Hazelcast " +
            "cluster will only work with localhost instances");

        System.setProperty("hazelcast.local.localAddress", "127.0.0.1");
        config.getNetworkConfig().setPort(serverProperties.getPort() + 5701);
        config.getNetworkConfig().getJoin().getTcpIpConfig().setEnabled(true);
        for (ServiceInstance instance : discoveryClient.getInstances(serviceId)) {
          String clusterMember = "127.0.0.1:" + (instance.getPort() + 5701);
          log.debug("Adding Hazelcast (dev) cluster member {}", clusterMember);
          config.getNetworkConfig().getJoin().getTcpIpConfig().addMember(clusterMember);
        }
      } else if (env
          .acceptsProfiles(Profiles.of(SpringProfileConstants.SPRING_PROFILE_DEVELOPMENT_GCP))) {
        log.debug(
            "Application is running with the \"devgcp\" profile, Hazelcast cluster will use swarm discovery");

        config.setProperty("hazelcast.discovery.enabled", "true");
        config.setProperty("hazelcast.shutdownhook.enabled", "true");
        config.setProperty("hazelcast.socket.bind.any", "false");

        config.getNetworkConfig().getJoin().getAwsConfig().setEnabled(false);
        config.getNetworkConfig().getJoin().getTcpIpConfig().setEnabled(false);

        DiscoveryStrategyConfig discoveryStrategyConfig = new DiscoveryStrategyConfig(
            "org.bitsofinfo.hazelcast.discovery.docker.swarm.DockerSwarmDiscoveryStrategy");
        discoveryStrategyConfig
            .addProperty("docker-network-names", env.getProperty("dockerNetworkNames"));
        discoveryStrategyConfig
            .addProperty("docker-service-names", env.getProperty("dockerServiceNames"));
        discoveryStrategyConfig
            .addProperty("hazelcast-peer-port", env.getProperty("hazelcastPeerPort"));

        DiscoveryConfig discoveryConfig = new DiscoveryConfig();
        discoveryConfig.addDiscoveryStrategyConfig(discoveryStrategyConfig);

        config.getNetworkConfig().getJoin().setDiscoveryConfig(discoveryConfig);

        SwarmMemberAddressProvider memberAddressProvider = new SwarmMemberAddressProvider(
            env.getProperty("dockerNetworkNames"), "", env.getProperty("dockerServiceNames"),
            Integer.parseInt(env.getProperty("hazelcastPeerPort")), true, true);

        MemberAddressProviderConfig memberAddressProviderConfig = new MemberAddressProviderConfig();
        memberAddressProviderConfig.setEnabled(true);
        memberAddressProviderConfig.setImplementation(memberAddressProvider);

        config.getNetworkConfig().setMemberAddressProviderConfig(memberAddressProviderConfig);

      } else { // Production configuration, one host per instance all using port 5701
        config.getNetworkConfig().setPort(5701);
        config.getNetworkConfig().getJoin().getTcpIpConfig().setEnabled(true);
        for (ServiceInstance instance : discoveryClient.getInstances(serviceId)) {
          String clusterMember = instance.getHost() + ":5701";
          log.debug("Adding Hazelcast (prod) cluster member {}", clusterMember);
          config.getNetworkConfig().getJoin().getTcpIpConfig().addMember(clusterMember);
        }
      }
    }
    config.getMapConfigs().put("default", initializeDefaultMapConfig(appProperties));

    // Full reference is available at: http://docs.hazelcast.org/docs/management-center/3.9/manual/html/Deploying_and_Starting.html
    config.setManagementCenterConfig(initializeDefaultManagementCenterConfig(appProperties));
    config.getMapConfigs()
        .put("org.wannagoframework.i18n.domain.*", initializeDomainMapConfig(appProperties));
    return Hazelcast.newHazelcastInstance(config);
  }

  private ManagementCenterConfig initializeDefaultManagementCenterConfig(
      AppProperties appProperties) {
    ManagementCenterConfig managementCenterConfig = new ManagementCenterConfig();
    managementCenterConfig
        .setEnabled(appProperties.getHazelcast().getManagementCenter().isEnabled());
    managementCenterConfig.setUrl(appProperties.getHazelcast().getManagementCenter().getUrl());
    managementCenterConfig
        .setUpdateInterval(appProperties.getHazelcast().getManagementCenter().getUpdateInterval());
    return managementCenterConfig;
  }

  private MapConfig initializeDefaultMapConfig(AppProperties appProperties) {
    MapConfig mapConfig = new MapConfig();

        /*
        Number of backups. If 1 is set as the backup-count for example,
        then all entries of the map will be copied to another JVM for
        fail-safety. Valid numbers are 0 (no backup), 1, 2, 3.
        */
    mapConfig.setBackupCount(appProperties.getHazelcast().getBackupCount());

        /*
        Valid values are:
        NONE (no eviction),
        LRU (Least Recently Used),
        LFU (Least Frequently Used).
        NONE is the default.
        */
    mapConfig.setEvictionPolicy(EvictionPolicy.LRU);

        /*
        Maximum size of the map. When max size is reached,
        map is evicted based on the policy defined.
        Any integer between 0 and Integer.MAX_VALUE. 0 means
        Integer.MAX_VALUE. Default is 0.
        */
    mapConfig.setMaxSizeConfig(new MaxSizeConfig(0, MaxSizeConfig.MaxSizePolicy.USED_HEAP_SIZE));

    return mapConfig;
  }

  private MapConfig initializeDomainMapConfig(AppProperties appProperties) {
    MapConfig mapConfig = new MapConfig();
    mapConfig.setTimeToLiveSeconds(appProperties.getHazelcast().getTimeToLiveSeconds());
    return mapConfig;
  }
}
