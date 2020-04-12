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

import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.wannagoframework.commons.endpoint.BaseEndpoint;
import org.wannagoframework.commons.utils.OrikaBeanMapper;
import org.wannagoframework.dto.serviceQuery.BaseRemoteQuery;
import org.wannagoframework.dto.serviceQuery.ServiceResult;
import org.wannagoframework.dto.serviceQuery.i18n.ImportI18NFileQuery;
import org.wannagoframework.i18n.service.I18nService;

/**
 * @author WannaGo Dev1.
 * @version 1.0
 * @since 2019-06-05
 */

@RestController
@RequestMapping("/i18NService")
public class I18NServiceEndpoint extends BaseEndpoint {

  private final I18nService i18nService;

  public I18NServiceEndpoint(I18nService i18nService,
      OrikaBeanMapper mapperFacade) {
    super(mapperFacade);
    this.i18nService = i18nService;
  }

  @PreAuthorize("#oauth2.hasScope('frontend')")
  @PostMapping(value = "/getI18NFile")
  public ResponseEntity<ServiceResult> getI18NFile(@RequestBody BaseRemoteQuery query) {
    String loggerPrefix = getLoggerPrefix("getI18NFile");
    try {
      return handleResult(loggerPrefix, i18nService.getI18NFile());
    } catch (Throwable t) {
      return handleResult(loggerPrefix, t);
    }
  }

  @PreAuthorize("#oauth2.hasScope('frontend')")
  @PostMapping(value = "/importI18NFile")
  public ResponseEntity<ServiceResult> importI18NFile(@RequestBody ImportI18NFileQuery query) {
    String loggerPrefix = getLoggerPrefix("importI18NFile");
    try {
      String result = i18nService.importI18NFile(query.getFileContent());
      if (result == null) {
        return handleResult(loggerPrefix);
      } else {
        return handleResult(loggerPrefix, result);
      }
    } catch (Throwable t) {
      return handleResult(loggerPrefix, t);
    }
  }
}