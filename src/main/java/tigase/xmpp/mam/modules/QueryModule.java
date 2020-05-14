/*
 * Tigase XMPP Server - The instant messaging server
 * Copyright (C) 2004 Tigase, Inc. (office@tigase.com)
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
package tigase.xmpp.mam.modules;

import tigase.component.PacketWriter;
import tigase.component.exceptions.ComponentException;
import tigase.component.exceptions.RepositoryException;
import tigase.component.modules.Module;
import tigase.criteria.Criteria;
import tigase.kernel.beans.Bean;
import tigase.kernel.beans.Inject;
import tigase.server.Packet;
import tigase.server.Priority;
import tigase.util.stringprep.TigaseStringprepException;
import tigase.xml.Element;
import tigase.xmpp.StanzaType;
import tigase.xmpp.mam.MAMRepository;
import tigase.xmpp.mam.Query;
import tigase.xmpp.mam.QueryParser;

/**
 * Implementation of module processing requests to retrieve items using XEP-0313: Message Archive Management
 * <br>
 * Created by andrzej on 19.07.2016.
 */
@Bean(name = "mamQueryModule", active = true)
public class QueryModule
		implements Module {

	private static final String[] FEATURES = {"urn:xmpp:mam:1"};
	@Inject(bean = "mamItemHandler")
	private MAMRepository.ItemHandler itemHandler;
	@Inject
	private MAMRepository mamRepository;
	@Inject
	private PacketWriter packetWriter;
	@Inject(bean = "mamQueryParser")
	private QueryParser queryParser;

	@Override
	public String[] getFeatures() {
		return FEATURES;
	}

	@Override
	public Criteria getModuleCriteria() {
		return null;
	}

	@Override
	public boolean canHandle(Packet packet) {
		return packet.getElement().findChild(child -> child.getName() == "query" && isXMLNSSupported(child.getXMLNS())) != null &&
				packet.getType() == StanzaType.set;
	}

	protected boolean isXMLNSSupported(String xmlns) {
		return queryParser.getXMLNSs().contains(xmlns);
	}

	@Override
	public void process(Packet packet) throws ComponentException, TigaseStringprepException {
		Query query = mamRepository.newQuery();
		query = queryParser.parseQuery(query, packet);
		try {
			mamRepository.queryItems(query, itemHandler);
		} catch (RepositoryException ex) {
			throw new RuntimeException("Error retrieving messages from database", ex);
		}

		Element fin = new Element("fin");
		fin.setXMLNS(query.getXMLNS());
		fin.addChild(query.getRsm().toElement());
		if (query.getRsm().getIndex() + query.getRsm().getMax() >= query.getRsm().getCount()) {
			fin.setAttribute("complete", "true");
		}

		Packet result = packet.okResult(fin, 0);
		result.setPacketFrom(null);
		result.setPriority(Priority.LOW);

		packetWriter.write(result);
	}
}
