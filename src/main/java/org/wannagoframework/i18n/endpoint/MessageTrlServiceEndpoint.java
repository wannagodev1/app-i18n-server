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


package org.wannagoframework.i18n.endpoint;

import java.util.List;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.wannagoframework.commons.endpoint.BaseEndpoint;
import org.wannagoframework.commons.utils.OrikaBeanMapper;
import org.wannagoframework.dto.serviceQuery.ServiceResult;
import org.wannagoframework.dto.serviceQuery.generic.DeleteByIdQuery;
import org.wannagoframework.dto.serviceQuery.generic.GetByIdQuery;
import org.wannagoframework.dto.serviceQuery.generic.SaveQuery;
import org.wannagoframework.dto.serviceQuery.i18n.FindByIso3Query;
import org.wannagoframework.dto.serviceQuery.i18n.GetByNameAndIso3Query;
import org.wannagoframework.dto.serviceQuery.i18n.messageTrl.CountByMessageQuery;
import org.wannagoframework.dto.serviceQuery.i18n.messageTrl.FindByMessageQuery;
import org.wannagoframework.i18n.domain.MessageTrl;
import org.wannagoframework.i18n.service.MessageTrlService;

/**
 * @author WannaGo Dev1.
 * @version 1.0
 * @since 2019-06-05
 */
@CrossOrigin("http://localhost:4200")
@RestController
@RequestMapping("/messageTrlService")
public class MessageTrlServiceEndpoint extends BaseEndpoint {

  private final MessageTrlService messageTrlService;

  public MessageTrlServiceEndpoint(MessageTrlService messageTrlService,
      OrikaBeanMapper mapperFacade) {
    super(mapperFacade);
    this.messageTrlService = messageTrlService;
  }

  @PreAuthorize("#oauth2.hasAnyScope('frontend','mobile','backend')")
  @PostMapping(value = "/findByMessage")
  public ResponseEntity<ServiceResult> findByMessage(@RequestBody FindByMessageQuery query) {
    String loggerPrefix = getLoggerPrefix("findByMessage");
    try {
      List<MessageTrl> result = messageTrlService.findByMessage(query.getMessageId());

      return handleResult(loggerPrefix, mapperFacade.mapAsList(result,
          org.wannagoframework.dto.domain.i18n.MessageTrl.class, getOrikaContext(query)));
    } catch (Throwable t) {
      return handleResult(loggerPrefix, t);
    }
  }

  @PreAuthorize("#oauth2.hasAnyScope('frontend','mobile','backend')")
  @PostMapping(value = "/countByMessage")
  public ResponseEntity<ServiceResult> countByMessage(@RequestBody CountByMessageQuery query) {
    String loggerPrefix = getLoggerPrefix("countByMessage");
    try {
      return handleResult(loggerPrefix, messageTrlService
          .countByMessage(query.getMessageId()));
    } catch (Throwable t) {
      return handleResult(loggerPrefix, t);
    }
  }

  @PreAuthorize("#oauth2.hasAnyScope('frontend','mobile','backend')")
  @PostMapping(value = "/findByIso3")
  public ResponseEntity<ServiceResult> findByIso3(@RequestBody FindByIso3Query query) {
    String loggerPrefix = getLoggerPrefix("findByIso3");
    try {
      List<MessageTrl> result = messageTrlService
          .getByIso3Language(query.getIso3Language());

      return handleResult(loggerPrefix, mapperFacade
          .mapAsList(result, org.wannagoframework.dto.domain.i18n.MessageTrl.class, getOrikaContext(query)));
    } catch (Throwable t) {
      return handleResult(loggerPrefix, t);
    }
  }

  @PreAuthorize("#oauth2.hasAnyScope('frontend','mobile','backend')")
  @PostMapping(value = "/getByNameAndIso3")
  public ResponseEntity<ServiceResult> getByNameAndIso3(@RequestBody GetByNameAndIso3Query query) {
    String loggerPrefix = getLoggerPrefix("getByNameAndIso3");
    try {
      MessageTrl result = messageTrlService
          .getByNameAndIso3Language(query.getName(), query.getIso3Language());

      return handleResult(loggerPrefix, mapperFacade
          .map(result, org.wannagoframework.dto.domain.i18n.MessageTrl.class, getOrikaContext(query)));
    } catch (Throwable t) {
      return handleResult(loggerPrefix, t);
    }
  }

  @PreAuthorize("#oauth2.hasAnyScope('frontend','mobile','backend')")
  @PostMapping(value = "/getById")
  public ResponseEntity<ServiceResult> getById(@RequestBody GetByIdQuery query) {
    String loggerPrefix = getLoggerPrefix("getById");
    try {
      return handleResult(loggerPrefix, mapperFacade.map(messageTrlService
              .load(query.getId()), org.wannagoframework.dto.domain.i18n.MessageTrl.class,
          getOrikaContext(query)));
    } catch (Throwable t) {
      return handleResult(loggerPrefix, t);
    }
  }

  @PreAuthorize("#oauth2.hasScope('frontend')")
  @PostMapping(value = "/save")
  public ResponseEntity<ServiceResult> save(
      @RequestBody SaveQuery<org.wannagoframework.dto.domain.i18n.MessageTrl> query) {
    String loggerPrefix = getLoggerPrefix("save");
    try {
      return handleResult(loggerPrefix, mapperFacade.map(messageTrlService
              .save(mapperFacade
                  .map(query.getEntity(), MessageTrl.class, getOrikaContext(query))),
          org.wannagoframework.dto.domain.i18n.MessageTrl.class, getOrikaContext(query)));
    } catch (Throwable t) {
      return handleResult(loggerPrefix, t);
    }
  }

  @PreAuthorize("#oauth2.hasScope('frontend')")
  @PostMapping(value = "/delete")
  public ResponseEntity<ServiceResult> delete(@RequestBody DeleteByIdQuery query) {
    String loggerPrefix = getLoggerPrefix("delete");
    try {
      messageTrlService
          .delete(query.getId());
      return handleResult(loggerPrefix);
    } catch (Throwable t) {
      return handleResult(loggerPrefix, t);
    }
  }
}