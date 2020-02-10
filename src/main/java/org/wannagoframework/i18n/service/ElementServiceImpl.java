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
import org.apache.commons.lang3.StringUtils;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.wannagoframework.i18n.domain.Element;
import org.wannagoframework.i18n.domain.ElementTrl;
import org.wannagoframework.i18n.repository.ElementRepository;

/**
 * @author WannaGo Dev1.
 * @version 1.0
 * @since 2019-07-16
 */
@Service
@Transactional(readOnly = true)
public class ElementServiceImpl implements ElementService {

  private final ElementRepository elementRepository;
  private final ElementTrlService elementTrlService;

  public ElementServiceImpl(ElementRepository elementRepository,
      ElementTrlService elementTrlService) {
    this.elementRepository = elementRepository;
    this.elementTrlService = elementTrlService;
  }

  @Override
  public Page<Element> findByNameLike(String name, Pageable pageable) {
    return elementRepository.findByNameLike(name, pageable);
  }

  @Override
  public long countByNameLike(String name) {
    return elementRepository.countByNameLike(name);
  }

  @Override
  public Page<Element> findAnyMatching(String filter, Pageable pageable) {
    if (StringUtils.isBlank(filter)) {
      return elementRepository.findAll(pageable);
    } else {
      return elementRepository.findAnyMatching(filter, pageable);
    }
  }

  @Override
  public long countAnyMatching(String filter) {
    if (StringUtils.isBlank(filter)) {
      return elementRepository.count();
    } else {
      return elementRepository.countAnyMatching(filter);
    }
  }

  @Override
  @Transactional
  public Element save(Element entity) {
    List<ElementTrl> translations = entity.getTranslations();
    entity = elementRepository.save(entity);
    for (ElementTrl elementTrl : translations) {
      elementTrl.setElement(entity);
    }
    if (translations.size() > 0) {
      entity.setTranslations(elementTrlService.saveAll(translations));
    }

    return entity;
  }

  @Override
  @Transactional
  public void delete(Element entity) {
    List<ElementTrl> elementTrls = elementTrlService.findByElement(entity.getId());
    if (elementTrls.size() > 0) {
      elementTrlService.deleteAll(elementTrls);
    }

    elementRepository.delete(entity);
  }

  @Override
  public JpaRepository<Element, Long> getRepository() {
    return elementRepository;
  }
}
