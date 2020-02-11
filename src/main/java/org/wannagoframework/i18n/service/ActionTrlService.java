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


package org.wannagoframework.i18n.service;

import java.util.List;
import org.wannagoframework.i18n.domain.ActionTrl;

/**
 * @author WannaGo Dev1.
 * @version 1.0
 * @since 2019-07-16
 */
public interface ActionTrlService extends CrudService<ActionTrl> {

  List<ActionTrl> findByAction(Long actionId);

  long countByAction(Long actionId);

  ActionTrl getByNameAndIso3Language(String name, String iso3Language);

  List<ActionTrl> getByIso3Language(String iso3Language);

  List<ActionTrl> saveAll(List<ActionTrl> translations);

  void deleteAll(List<ActionTrl> actionTrls);

  void postUpdate(ActionTrl actionTrl);
}
