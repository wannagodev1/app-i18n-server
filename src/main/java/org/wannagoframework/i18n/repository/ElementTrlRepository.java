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
import org.springframework.stereotype.Repository;
import org.wannagoframework.i18n.domain.Element;
import org.wannagoframework.i18n.domain.ElementTrl;

/**
 * @author WannaGo Dev1.
 * @version 1.0
 * @since 2019-07-16
 */
@Repository
public interface ElementTrlRepository extends JpaRepository<ElementTrl, Long> {

  List<ElementTrl> findByElement(Element element);

  long countByElement(Element element);

  List<ElementTrl> findByIso3Language(String iso3Language);

  Optional<ElementTrl> getByElementAndIso3Language(Element element, String iso3Language);

  Optional<ElementTrl> getByElementAndIsDefault(Element element, Boolean isDefault);
}
