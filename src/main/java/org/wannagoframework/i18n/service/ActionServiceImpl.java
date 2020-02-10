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
import org.wannagoframework.i18n.domain.Action;
import org.wannagoframework.i18n.domain.ActionTrl;
import org.wannagoframework.i18n.repository.ActionRepository;

/**
 * @author WannaGo Dev1.
 * @version 1.0
 * @since 2019-07-16
 */
@Service
@Transactional(readOnly = true)
public class ActionServiceImpl implements ActionService {

  private final ActionRepository actionRepository;
  private final ActionTrlService actionTrlService;

  public ActionServiceImpl(ActionRepository actionRepository,
      ActionTrlService actionTrlService) {
    this.actionRepository = actionRepository;
    this.actionTrlService = actionTrlService;
  }

  @Override
  public Page<Action> findByNameLike(String name, Pageable pageable) {
    return actionRepository.findByNameLike(name, pageable);
  }

  @Override
  public long countByNameLike(String name) {
    return actionRepository.countByNameLike(name);
  }

  @Override
  public Page<Action> findAnyMatching(String filter, Pageable pageable) {
    if (StringUtils.isBlank(filter)) {
      return actionRepository.findAll(pageable);
    } else {
      return actionRepository.findAnyMatching(filter, pageable);
    }
  }

  @Override
  public long countAnyMatching(String filter) {
    if (StringUtils.isBlank(filter)) {
      return actionRepository.count();
    } else {
      return actionRepository.countAnyMatching(filter);
    }
  }

  @Override
  @Transactional
  public Action save(Action entity) {
    List<ActionTrl> translations = entity.getTranslations();
    entity = actionRepository.save(entity);
    for (ActionTrl actionTrl : translations) {
      actionTrl.setAction(entity);
    }
    if (translations.size() > 0) {
      entity.setTranslations(actionTrlService.saveAll(translations));
    }

    return entity;
  }

  @Override
  @Transactional
  public void delete(Action entity) {
    List<ActionTrl> actionTrls = actionTrlService.findByAction(entity.getId());
    if (actionTrls.size() > 0) {
      actionTrlService.deleteAll(actionTrls);
    }

    actionRepository.delete(entity);
  }

  @Override
  public JpaRepository<Action, Long> getRepository() {
    return actionRepository;
  }
}
