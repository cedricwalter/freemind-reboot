/*FreeMind - A Program for creating and viewing Mindmaps
*Copyright (C) 2000-2014 Joerg Mueller, Daniel Polansky, Christian Foltin, Dimitri Polivaev and others.
*
*See COPYING for Details
*
*This program is free software; you can redistribute it and/or
*modify it under the terms of the GNU General Public License
*as published by the Free Software Foundation; either version 2
*of the License, or (at your option) any later version.
*
*This program is distributed in the hope that it will be useful,
*but WITHOUT ANY WARRANTY; without even the implied warranty of
*MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
*GNU General Public License for more details.
*
*You should have received a copy of the GNU General Public License
*along with this program; if not, write to the Free Software
*Foundation, Inc., 59 Temple Place - Suite 330, Boston, MA  02111-1307, USA.
*/

package freemind.modes.mindmapmode.actions.xml.actors;

import freemind.modes.MapFeedback;
import freemind.modes.MindMapNode;
import freemind.modes.NodeAdapter;
import freemind.modes.mindmapmode.MindMapController;
import freemind.modes.mindmapmode.actions.NodeActorXml;
import freemind.modes.mindmapmode.actions.xml.ActionPair;
import freemind.modes.mindmapmode.actions.xml.ActorXml;

/**
 * @author foltin
 * @date 16.03.2014
 */
public abstract class XmlActorAdapter implements NodeActorXml {

	protected MapFeedback mMapFeedback;
	private ActorXml mActor;

	/**
	 * 
	 */
	public XmlActorAdapter(MapFeedback pMapFeedback) {
		mMapFeedback = pMapFeedback;
		addActor(this);
	}

	
	/**
	 * @deprecated replaced by {@link XmlActorAdapter#getMapFeedback()}
	 * @return
	 */
	@Deprecated
	protected MindMapController getModeController() {
		return (MindMapController) mMapFeedback;
	}

	/**
	 * @return the mapFeedback
	 */
	public MapFeedback getMapFeedback() {
		return mMapFeedback;
	}
	
	/**
	 * @param pActionPair
	 */
	protected void execute(ActionPair pActionPair) {
		getModeController().doTransaction(getDoActionClass().getName(), pActionPair);
		
	}

	/**
	 * @param pNodeId
	 * @return
	 */
	protected NodeAdapter getNodeFromID(String pNodeId) {
		return getModeController().getNodeFromID(pNodeId);
	}


	/**
	 * @return
	 */
	protected MindMapNode getSelected() {
		return getModeController().getSelected();
	}

	/**
	 * @param pSelected
	 * @return
	 */
	protected String getNodeID(MindMapNode pNode) {
		return getModeController().getNodeID(pNode);
	}

	public void addActor(ActorXml actor) {
		this.mActor = actor;
		if (actor != null) {
			// registration:
			getModeController().getActionFactory().registerActor(actor,
					actor.getDoActionClass());
		}
	}
	
}