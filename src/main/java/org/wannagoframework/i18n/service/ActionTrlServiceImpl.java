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

import java.io.File;
import java.io.IOException;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Optional;
import javax.annotation.PostConstruct;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.usermodel.WorkbookFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.Assert;
import org.wannagoframework.commons.utils.HasLogger;
import org.wannagoframework.i18n.domain.Action;
import org.wannagoframework.i18n.domain.ActionTrl;
import org.wannagoframework.i18n.domain.Element;
import org.wannagoframework.i18n.domain.ElementTrl;
import org.wannagoframework.i18n.repository.ActionRepository;
import org.wannagoframework.i18n.repository.ActionTrlRepository;

/**
 * @author WannaGo Dev1.
 * @version 1.0
 * @since 2019-07-16
 */
@Service
@Transactional(readOnly = true)
public class ActionTrlServiceImpl implements ActionTrlService, HasLogger {

  private final ActionRepository actionRepository;
  private final ActionTrlRepository actionTrlRepository;

  private boolean hasBootstrapped = false;

  @Value("${wannaplay.bootstrap.i18n.file}")
  private String bootstrapFile;

  @Value("${wannaplay.bootstrap.i18n.enabled}")
  private Boolean isBootstrapEnabled;

  public ActionTrlServiceImpl(ActionRepository actionRepository,
      ActionTrlRepository actionTrlRepository) {
    this.actionRepository = actionRepository;
    this.actionTrlRepository = actionTrlRepository;
  }

  @Transactional
  @EventListener(ApplicationReadyEvent.class)
  protected void postLoad() {
    bootstrapActions();
  }

  @Override
  public List<ActionTrl> findByAction(Long actionId) {
    Optional<Action> _action = actionRepository.findById(actionId);
    if (_action.isPresent()) {
      return actionTrlRepository.findByAction(_action.get());
    } else {
      return Collections.emptyList();
    }
  }

  @Override
  public long countByAction(Long actionId) {
    Optional<Action> _action = actionRepository.findById(actionId);
    if (_action.isPresent()) {
      return actionTrlRepository.countByAction(_action.get());
    } else {
      return 0;
    }
  }

  @Transactional
  @Override
  public ActionTrl getByNameAndIso3Language(String name, String iso3Language) {
    String loggerPrefix = getLoggerPrefix("getByNameAndIso3Language");

    Assert.notNull(name, "Name mandatory");
    Assert.notNull(iso3Language, "ISO3 language is mandatory");

    Optional<Action> _action = actionRepository.getByName(name);
    Action action;
    if (!_action.isPresent()) {
      logger().warn(loggerPrefix + "Action '" + name + "' not found, create a new one");
      action = new Action();
      action.setName(name);
      action.setIsTranslated(false);
      action = actionRepository.save(action);
    } else {
      action = _action.get();
    }
    Optional<ActionTrl> _actionTrl = actionTrlRepository
        .getByActionAndIso3Language(action, iso3Language);
    if (_actionTrl.isPresent()) {
      return _actionTrl.get();
    } else {
      logger().warn(loggerPrefix + "Action '" + name + "', '" + iso3Language
          + "' language translation not found, create a new one");
      ActionTrl actionTrl = new ActionTrl();
      actionTrl.setIso3Language(iso3Language);
      actionTrl.setAction(action);

      Optional<ActionTrl> _defaultActionTrl = actionTrlRepository
          .getByActionAndIsDefault(action, true);
      if (_defaultActionTrl.isPresent()) {
        actionTrl.setValue(_defaultActionTrl.get().getValue());
      } else {
        actionTrl.setValue(name);
      }
      actionTrl.setIsTranslated(false);

      actionTrl = actionTrlRepository.save(actionTrl);
      return actionTrl;
    }
  }

  @Override
  public List<ActionTrl> getByIso3Language(String iso3Language) {
    Assert.notNull(iso3Language, "ISO3 language is mandatory");

    return actionTrlRepository.findByIso3Language(iso3Language);
  }

  @Override
  public List<ActionTrl> saveAll(List<ActionTrl> translations) {
    return actionTrlRepository.saveAll(translations);
  }

  @Override
  @Transactional
  public void deleteAll(List<ActionTrl> actionTrls) {
    actionTrlRepository.deleteAll(actionTrls);
  }

  @Override
  public JpaRepository<ActionTrl, Long> getRepository() {
    return actionTrlRepository;
  }

  @Transactional
  @Override
  public void postUpdate(ActionTrl actionTrl) {
    boolean isAllTranslated = true;
    Action action = actionTrl.getAction();
    List<ActionTrl> trls = actionTrl.getAction().getTranslations();
    for (ActionTrl trl : trls) {
      if (!trl.getIsTranslated()) {
        isAllTranslated = false;
        break;
      }
    }
    if ( isAllTranslated && ! action.getIsTranslated() ) {
      action.setIsTranslated(true);
      actionRepository.save(action );
    }
     else if ( ! isAllTranslated && action.getIsTranslated()  ) {
      action.setIsTranslated(false);
      actionRepository.save(action );
    }
  }

  @Transactional
  public synchronized void bootstrapActions() {
    if (hasBootstrapped || !isBootstrapEnabled) {
      return;
    }

    String loggerPrefix = getLoggerPrefix("bootstrapActions");

    try (Workbook workbook = WorkbookFactory.create(new File(bootstrapFile))) {

      Sheet sheet = workbook.getSheet("actions");

      logger().info(loggerPrefix + sheet.getPhysicalNumberOfRows() + " rows");

      Iterator<Row> rowIterator = sheet.rowIterator();
      int rowIndex = 0;

      while (rowIterator.hasNext()) {
        Row row = rowIterator.next();
        if (rowIndex == 0) {
          rowIndex++;
          continue;
        }

        if (rowIndex % 10 == 0) {
          logger().info(loggerPrefix + "Handle row " + rowIndex++);
        }

        int colIdx = 1;
        Cell name0Cell = row.getCell(colIdx++);
        Cell name1Cell = row.getCell(colIdx++);
        Cell name2Cell = row.getCell(colIdx++);
        Cell langCell = row.getCell(colIdx++);
        Cell valueCell = row.getCell(colIdx);

        if (langCell == null) {
          logger().error(loggerPrefix + "Empty value for language, skip");
          continue;
        }

        if (name0Cell == null) {
          logger().error(loggerPrefix + "Empty value for name, skip");
          continue;
        }

        String name = name0Cell.getStringCellValue();
        if (name1Cell != null) {
          name += "." + name1Cell.getStringCellValue();
        }
        if (name2Cell != null) {
          name += "." + name2Cell.getStringCellValue();
        }

        String language = langCell.getStringCellValue();

        Optional<Action> _action = actionRepository.getByName(name);
        Action action;
        if (!_action.isPresent()) {
          action = new Action();
          action.setName(name);
          action.setIsTranslated(true);
          action = actionRepository.save(action);
        } else {
          action = _action.get();
          action.setIsTranslated(true);
          action = actionRepository.save(action);
        }

        Optional<ActionTrl> _actionTrl = actionTrlRepository
            .getByActionAndIso3Language(action, language);
        ActionTrl actionTrl;
        if (!_actionTrl.isPresent()) {
          actionTrl = new ActionTrl();
          actionTrl.setValue(valueCell == null ? "" : valueCell.getStringCellValue());
          actionTrl.setIso3Language(language);
          actionTrl.setAction(action);
          actionTrl.setIsTranslated(true);

          actionTrlRepository.save(actionTrl);
        } else {
          actionTrl = _actionTrl.get();
          if ( ! actionTrl.getIsTranslated() ) {
            actionTrl.setValue(valueCell == null ? "" : valueCell.getStringCellValue());
            actionTrl.setIso3Language(language);
            actionTrl.setAction(action);
            actionTrl.setIsTranslated(true);

            actionTrlRepository.save(actionTrl);
          }
        }
      }
    } catch (IOException e) {
      e.printStackTrace();
    }

    logger().info(loggerPrefix + "Done");

    hasBootstrapped = true;
  }
}
