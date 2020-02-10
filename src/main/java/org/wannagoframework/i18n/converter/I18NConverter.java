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


package org.wannagoframework.i18n.converter;

import ma.glasnost.orika.CustomMapper;
import ma.glasnost.orika.MappingContext;
import org.springframework.context.annotation.Bean;
import org.springframework.data.domain.PageImpl;
import org.springframework.stereotype.Component;
import org.wannagoframework.commons.utils.HasLogger;
import org.wannagoframework.commons.utils.OrikaBeanMapper;
import org.wannagoframework.dto.utils.Page;
import org.wannagoframework.i18n.domain.Action;
import org.wannagoframework.i18n.domain.ActionTrl;
import org.wannagoframework.i18n.domain.Element;
import org.wannagoframework.i18n.domain.ElementTrl;
import org.wannagoframework.i18n.domain.Message;
import org.wannagoframework.i18n.domain.MessageTrl;

/**
 * @author WannaGo Dev1.
 * @version 1.0
 * @since 2019-06-05
 */
@Component
public class I18NConverter implements HasLogger {

  private final OrikaBeanMapper orikaBeanMapper;

  public I18NConverter(OrikaBeanMapper orikaBeanMapper) {
    this.orikaBeanMapper = orikaBeanMapper;
  }

  @Bean
  public void i18NConverters() {
    orikaBeanMapper.addMapper(Action.class, org.wannagoframework.dto.domain.i18n.Action.class);
    orikaBeanMapper
        .getClassMapBuilder(ActionTrl.class, org.wannagoframework.dto.domain.i18n.ActionTrl.class)
        .byDefault().customize(
        new CustomMapper<ActionTrl, org.wannagoframework.dto.domain.i18n.ActionTrl>() {
          @Override
          public void mapAtoB(ActionTrl a, org.wannagoframework.dto.domain.i18n.ActionTrl b,
              MappingContext context) {
            b.setName(a.getAction().getName());
          }
        }).register();

    orikaBeanMapper.addMapper(Element.class, org.wannagoframework.dto.domain.i18n.Element.class);
    orikaBeanMapper
        .getClassMapBuilder(ElementTrl.class, org.wannagoframework.dto.domain.i18n.ElementTrl.class)
        .byDefault().customize(
        new CustomMapper<ElementTrl, org.wannagoframework.dto.domain.i18n.ElementTrl>() {
          @Override
          public void mapAtoB(ElementTrl a, org.wannagoframework.dto.domain.i18n.ElementTrl b,
              MappingContext context) {
            b.setName(a.getElement().getName());
          }
        }).register();

    orikaBeanMapper.addMapper(Message.class, org.wannagoframework.dto.domain.i18n.Message.class);
    orikaBeanMapper
        .getClassMapBuilder(MessageTrl.class, org.wannagoframework.dto.domain.i18n.MessageTrl.class)
        .byDefault().customize(
        new CustomMapper<MessageTrl, org.wannagoframework.dto.domain.i18n.MessageTrl>() {
          @Override
          public void mapAtoB(MessageTrl a, org.wannagoframework.dto.domain.i18n.MessageTrl b,
              MappingContext context) {
            b.setName(a.getMessage().getName());
          }
        }).register();

    orikaBeanMapper.addMapper(PageImpl.class, Page.class);
    orikaBeanMapper.addMapper(Page.class, PageImpl.class);
  }
}