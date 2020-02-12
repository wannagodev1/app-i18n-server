/*
 *   ilem group CONFIDENTIAL
 *    __________________
 *
 *    [2019] ilem Group
 *    All Rights Reserved.
 *
 *    NOTICE:  All information contained herein is, and remains the property of "ilem Group"
 *    and its suppliers, if any. The intellectual and technical concepts contained herein are
 *    proprietary to "ilem Group" and its suppliers and may be covered by Morocco, Switzerland and Foreign
 *    Patents, patents in process, and are protected by trade secret or copyright law.
 *    Dissemination of this information or reproduction of this material is strictly forbidden unless
 *    prior written permission is obtained from "ilem Group".
 */

package org.wannagoframework.i18n.listeners;

import javax.persistence.PostPersist;
import javax.persistence.PostRemove;
import javax.persistence.PostUpdate;
import org.springframework.stereotype.Component;
import org.wannagoframework.commons.utils.SpringApplicationContext;
import org.wannagoframework.i18n.domain.ActionTrl;
import org.wannagoframework.i18n.service.ActionTrlService;

/**
 * @author Alexandre Clavaud.
 * @version 1.0
 * @since 2/10/20
 */
@Component
public class ActionTrlListener {

  private ActionTrlService actionTrlService;

  @PostUpdate
  @PostPersist
  public void postUpdate(ActionTrl actionTrl) {
    if ( getActionTrlService() != null )
    getActionTrlService().postUpdate(actionTrl);
  }

  protected ActionTrlService getActionTrlService() {
    if (actionTrlService == null) {
      actionTrlService = SpringApplicationContext.getBean(ActionTrlService.class);
    }
    return actionTrlService;
  }

}
