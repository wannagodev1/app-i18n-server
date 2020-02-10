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
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.Assert;
import org.wannagoframework.commons.utils.HasLogger;
import org.wannagoframework.i18n.domain.Element;
import org.wannagoframework.i18n.domain.ElementTrl;
import org.wannagoframework.i18n.repository.ElementRepository;
import org.wannagoframework.i18n.repository.ElementTrlRepository;

/**
 * @author WannaGo Dev1.
 * @version 1.0
 * @since 2019-07-16
 */
@Service
@Transactional(readOnly = true)
public class ElementTrlServiceImpl implements ElementTrlService, HasLogger {

  private final ElementRepository elementRepository;
  private final ElementTrlRepository elementTrlRepository;

  private boolean hasBootstrapped = false;

  @Value("${wannaplay.bootstrap.i18n.file}")
  private String bootstrapFile;

  @Value("${wannaplay.bootstrap.i18n.enabled}")
  private Boolean isBootstrapEnabled;

  public ElementTrlServiceImpl(ElementRepository elementRepository,
      ElementTrlRepository elementTrlRepository) {
    this.elementRepository = elementRepository;
    this.elementTrlRepository = elementTrlRepository;
  }

  @Transactional
  @PostConstruct
  protected void postLoad() {
    bootstrapElements();
  }

  @Override
  public List<ElementTrl> findByElement(Long elementId) {
    Optional<Element> _element = elementRepository.findById(elementId);
    if (_element.isPresent()) {
      return elementTrlRepository.findByElement(_element.get());
    } else {
      return Collections.emptyList();
    }
  }

  @Override
  public long countByElement(Long elementId) {
    Optional<Element> _element = elementRepository.findById(elementId);
    if (_element.isPresent()) {
      return elementTrlRepository.countByElement(_element.get());
    } else {
      return 0;
    }
  }

  @Transactional
  @Override
  public ElementTrl getByNameAndIso3Language(String name, String iso3Language) {
    String loggerPrefix = getLoggerPrefix("getByNameAndIso3Language");

    Assert.notNull(name, "Name mandatory");
    Assert.notNull(iso3Language, "ISO3 language is mandatory");

    Optional<Element> _element = elementRepository.getByName(name);
    Element element;
    if (!_element.isPresent()) {
      logger().warn(loggerPrefix + "Element '" + name + "' not found, create a new one");
      element = new Element();
      element.setName(name);
      element.setIsTranslated(false);
      element = elementRepository.save(element);
    } else {
      element = _element.get();
    }
    Optional<ElementTrl> _elementTrl = elementTrlRepository
        .getByElementAndIso3Language(element, iso3Language);
    if (_elementTrl.isPresent()) {
      return _elementTrl.get();
    } else {
      logger().warn(loggerPrefix + "Element '" + name + "', '" + iso3Language
          + "' language translation not found, create a new one");
      ElementTrl elementTrl = new ElementTrl();
      elementTrl.setIso3Language(iso3Language);
      elementTrl.setElement(element);

      Optional<ElementTrl> _defaultElementTrl = elementTrlRepository
          .getByElementAndIsDefault(element, true);
      if (_defaultElementTrl.isPresent()) {
        elementTrl.setValue(_defaultElementTrl.get().getValue());
      } else {
        elementTrl.setValue(name);
      }
      elementTrl.setIsTranslated(false);

      elementTrl = elementTrlRepository.save(elementTrl);
      return elementTrl;
    }
  }

  @Override
  public List<ElementTrl> getByIso3Language(String iso3Language) {
    Assert.notNull(iso3Language, "ISO3 language is mandatory");

    return elementTrlRepository.findByIso3Language(iso3Language);
  }

  @Override
  public List<ElementTrl> saveAll(List<ElementTrl> translations) {
    return elementTrlRepository.saveAll(translations);
  }

  @Override
  @Transactional
  public void deleteAll(List<ElementTrl> elementTrls) {
    elementTrlRepository.deleteAll(elementTrls);
  }

  @Override
  public JpaRepository<ElementTrl, Long> getRepository() {
    return elementTrlRepository;
  }

  @Transactional
  public synchronized void bootstrapElements() {
    if (hasBootstrapped || !isBootstrapEnabled) {
      return;
    }

    String loggerPrefix = getLoggerPrefix("bootstrapElements");

    try (Workbook workbook = WorkbookFactory.create(new File(bootstrapFile))) {

      Sheet sheet = workbook.getSheet("elements");

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
        Cell name3Cell = row.getCell(colIdx++);
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
        if (name3Cell != null) {
          name += "." + name3Cell.getStringCellValue();
        }

        String language = langCell.getStringCellValue();

        Optional<Element> _element = elementRepository.getByName(name);
        Element element;
        if (!_element.isPresent()) {
          element = new Element();
          element.setName(name);
          element.setIsTranslated(true);
          element = elementRepository.save(element);
        } else {
          element = _element.get();
        }

        Optional<ElementTrl> _elementTrl = elementTrlRepository
            .getByElementAndIso3Language(element, language);
        ElementTrl elementTrl;
        if (!_elementTrl.isPresent()) {
          elementTrl = new ElementTrl();
          elementTrl.setValue(valueCell == null ? "" : valueCell.getStringCellValue());
          elementTrl.setIso3Language(language);
          elementTrl.setElement(element);
          elementTrl.setIsTranslated(true);

          elementTrlRepository.save(elementTrl);
        }
      }
    } catch (IOException e) {
      e.printStackTrace();
    }

    logger().info(loggerPrefix + "Done");

    hasBootstrapped = true;
  }
}
