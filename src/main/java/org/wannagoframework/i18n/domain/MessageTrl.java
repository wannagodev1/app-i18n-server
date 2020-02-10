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

/**
 * @author WannaGo Dev1.
 * @version 1.0
 * @since 2019-03-30
 */
@Data
@EqualsAndHashCode(callSuper = true)
@Entity
@Cache(usage = CacheConcurrencyStrategy.TRANSACTIONAL)
@TableGenerator(name = "MessageTrlKeyGen", table = "Sequence", pkColumnName = "COLUMN_NAME", pkColumnValue = "MESSAGE_TRL_ID", valueColumnName = "SEQ_VAL", initialValue = 0, allocationSize = 1)
public class MessageTrl extends EntityTranslation {

  @Id
  @GeneratedValue(strategy = GenerationType.TABLE, generator = "MessageTrlKeyGen")
  private Long id;

  @ManyToOne
  @JoinColumn(name = "MESSAGE_ID", nullable = false)
  private Message message;

  /**
   * ChatMessage Value
   */
  private String value;
}
