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


package org.wannagoframework.i18n.repository;

import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import org.wannagoframework.i18n.domain.Action;
import org.wannagoframework.i18n.domain.ActionTrl;

/**
 * @author WannaGo Dev1.
 * @version 1.0
 * @since 2019-07-16
 */
@Repository
public interface ActionTrlRepository extends JpaRepository<ActionTrl, Long> {

  List<ActionTrl> findByAction(Action action);

  long countByAction(Action action);

  List<ActionTrl> findByIso3Language(String iso3Language);

  @Query("SELECT distinct(iso3Language) from ActionTrl order by iso3Language")
  List<String> getIso3Languages();

  Optional<ActionTrl> getByActionAndIso3Language(Action action, String iso3Language);

  Optional<ActionTrl> getByActionAndIsDefault(Action action, Boolean isDefault);
}
