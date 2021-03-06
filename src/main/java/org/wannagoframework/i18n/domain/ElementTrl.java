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


package org.wannagoframework.i18n.domain;

import javax.persistence.Entity;
import javax.persistence.EntityListeners;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.TableGenerator;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;
import org.wannagoframework.i18n.listeners.ActionTrlListener;
import org.wannagoframework.i18n.listeners.ElementTrlListener;

/**
 * @author WannaGo Dev1.
 * @version 1.0
 * @since 2019-03-30
 */
@Data
@EqualsAndHashCode(callSuper = true)
@Entity
@Cache(usage = CacheConcurrencyStrategy.TRANSACTIONAL)
@TableGenerator(name = "ElementTrlKeyGen", table = "Sequence", pkColumnName = "COLUMN_NAME", pkColumnValue = "ELEMENT_TRL_ID", valueColumnName = "SEQ_VAL", initialValue = 0, allocationSize = 1)
@EntityListeners(ElementTrlListener.class)
public class ElementTrl extends EntityTranslation {

  @Id
  @GeneratedValue(strategy = GenerationType.TABLE, generator = "ElementTrlKeyGen")
  private Long id;

  @ManyToOne
  @JoinColumn(name = "ELEMENT_ID", nullable = false)
  private Element element;
  /**
   * Element Value
   */
  private String value;

  private String tooltip;
}
