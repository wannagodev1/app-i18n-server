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

import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import org.wannagoframework.i18n.domain.Message;

/**
 * @author WannaGo Dev1.
 * @version 1.0
 * @since 2019-07-16
 */
@Repository
public interface MessageRepository extends JpaRepository<Message, Long> {

  Optional<Message> getByName(String name);

  Page<Message> findByNameLike(String name, Pageable pageable);

  long countByNameLike(String name);

  @Query("SELECT DISTINCT a FROM Message a INNER JOIN MessageTrl t ON a.id = t.message.id WHERE a.name like :filter or t.value like :filter")
  Page<Message> findAnyMatching(String filter, Pageable pageable);

  @Query("SELECT COUNT(DISTINCT a) FROM Message a INNER JOIN MessageTrl t ON a.id = t.message.id WHERE a.name like :filter or t.value like :filter")
  long countAnyMatching(String filter);
}
