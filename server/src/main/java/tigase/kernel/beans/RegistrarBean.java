/*
 * RegistrarBean.java
 *
 * Tigase Jabber/XMPP Server
 * Copyright (C) 2004-2017 "Tigase, Inc." <office@tigase.com>
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as published by
 * the Free Software Foundation, version 3 of the License.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Affero General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with this program. Look for COPYING file in the top folder.
 * If not, see http://www.gnu.org/licenses/.
 */
package tigase.kernel.beans;

import tigase.kernel.core.Kernel;

/**
 * Interface which needs to be implemented by bean classes which are also Registrars.
 * <p>
 * Normal implementations of Registrars cannot be same class as bean inside newly created kernel - with
 * <code>RegistrarBean</code> it is possible.
 * <p>
 * Created by andrzej on 05.03.2016.
 */
public interface RegistrarBean {

	/**
	 * Method called when bean is being registered allowing developer to programatically register other beans.
	 *
	 * @param kernel - instance from local scope
	 */
	void register(Kernel kernel);

	/**
	 * Method called while bean is being unregistered.
	 *
	 * @param kernel - instance from local scope
	 */
	void unregister(Kernel kernel);

}
