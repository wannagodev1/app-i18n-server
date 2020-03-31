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
import org.wannagoframework.dto.serviceQuery.i18n.actionTrl.CountByActionQuery;
import org.wannagoframework.dto.serviceQuery.i18n.actionTrl.FindByActionQuery;
import org.wannagoframework.i18n.domain.ActionTrl;
import org.wannagoframework.i18n.service.ActionTrlService;

/**
 * @author WannaGo Dev1.
 * @version 1.0
 * @since 2019-06-05
 */

@CrossOrigin("http://localhost:4200")
@RestController
@RequestMapping("/actionTrlService")
public class ActionTrlServiceEndpoint extends BaseEndpoint {

  private final ActionTrlService actionTrlService;

  public ActionTrlServiceEndpoint(ActionTrlService actionTrlService,
      OrikaBeanMapper mapperFacade) {
    super(mapperFacade);
    this.actionTrlService = actionTrlService;
  }

  @PreAuthorize("#oauth2.hasAnyScope('frontend','mobile','backend')")
  @PostMapping(value = "/findByAction")
  public ResponseEntity<ServiceResult> findByAction(@RequestBody FindByActionQuery query) {
    String loggerPrefix = getLoggerPrefix("findByAction");
    try {
      List<ActionTrl> result = actionTrlService
          .findByAction(query.getActionId());

      return handleResult(loggerPrefix, mapperFacade.mapAsList(result,
          org.wannagoframework.dto.domain.i18n.ActionTrl.class, getOrikaContext(query)));
    } catch (Throwable t) {
      return handleResult(loggerPrefix, t);
    }
  }

  @PreAuthorize("#oauth2.hasAnyScope('frontend','mobile','backend')")
  @PostMapping(value = "/countByAction")
  public ResponseEntity<ServiceResult> countByAction(@RequestBody CountByActionQuery query) {
    String loggerPrefix = getLoggerPrefix("countByAction");
    try {
      return handleResult(loggerPrefix, actionTrlService
          .countByAction(query.getActionId()));
    } catch (Throwable t) {
      return handleResult(loggerPrefix, t);
    }
  }

  @PreAuthorize("#oauth2.hasAnyScope('frontend','mobile','backend')")
  @PostMapping(value = "/findByIso3")
  public ResponseEntity<ServiceResult> findByIso3(@RequestBody FindByIso3Query query) {
    String loggerPrefix = getLoggerPrefix("findByIso3");
    try {
      List<ActionTrl> result = actionTrlService
          .getByIso3Language(query.getIso3Language());

      return handleResult(loggerPrefix, mapperFacade
          .mapAsList(result, org.wannagoframework.dto.domain.i18n.ActionTrl.class, getOrikaContext(query)));
    } catch (Throwable t) {
      return handleResult(loggerPrefix, t);
    }
  }

  @PreAuthorize("#oauth2.hasAnyScope('frontend','mobile','backend')")
  @PostMapping(value = "/getByNameAndIso3")
  public ResponseEntity<ServiceResult> getByNameAndIso3(@RequestBody GetByNameAndIso3Query query) {
    String loggerPrefix = getLoggerPrefix("getByNameAndIso3");
    try {
      ActionTrl result = actionTrlService
          .getByNameAndIso3Language(query.getName(), query.getIso3Language());

      return handleResult(loggerPrefix, mapperFacade
          .map(result, org.wannagoframework.dto.domain.i18n.ActionTrl.class, getOrikaContext(query)));
    } catch (Throwable t) {
      return handleResult(loggerPrefix, t);
    }
  }

  @PreAuthorize("#oauth2.hasAnyScope('frontend','mobile','backend')")
  @PostMapping(value = "/getById")
  public ResponseEntity<ServiceResult> getById(@RequestBody GetByIdQuery query) {
    String loggerPrefix = getLoggerPrefix("getById");
    try {
      return handleResult(loggerPrefix, mapperFacade.map(actionTrlService
              .load(query.getId()), org.wannagoframework.dto.domain.i18n.ActionTrl.class,
          getOrikaContext(query)));
    } catch (Throwable t) {
      return handleResult(loggerPrefix, t);
    }
  }

  @PreAuthorize("#oauth2.hasScope('frontend')")
  @PostMapping(value = "/save")
  public ResponseEntity<ServiceResult> save(
      @RequestBody SaveQuery<org.wannagoframework.dto.domain.i18n.ActionTrl> query) {
    String loggerPrefix = getLoggerPrefix("save");
    try {
      return handleResult(loggerPrefix, mapperFacade.map(actionTrlService
              .save(mapperFacade
                  .map(query.getEntity(), ActionTrl.class, getOrikaContext(query))),
          org.wannagoframework.dto.domain.i18n.ActionTrl.class, getOrikaContext(query)));
    } catch (Throwable t) {
      return handleResult(loggerPrefix, t);
    }
  }

  @PreAuthorize("#oauth2.hasScope('frontend')")
  @PostMapping(value = "/delete")
  public ResponseEntity<ServiceResult> delete(@RequestBody DeleteByIdQuery query) {
    String loggerPrefix = getLoggerPrefix("delete");
    try {
      actionTrlService
          .delete(query.getId());
      return handleResult(loggerPrefix);
    } catch (Throwable t) {
      return handleResult(loggerPrefix, t);
    }
  }
}