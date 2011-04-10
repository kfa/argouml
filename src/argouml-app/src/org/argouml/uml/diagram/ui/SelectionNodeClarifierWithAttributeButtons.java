/* $Id$
 *******************************************************************************
 * Copyright (c) 2011 Contributors - see below
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    katharina
 *******************************************************************************
 */

package org.argouml.uml.diagram.ui;

import java.awt.Graphics;
import java.awt.Rectangle;
import java.awt.event.MouseEvent;

import javax.swing.Icon;

import org.argouml.application.helpers.ResourceLoaderWrapper;
import org.argouml.kernel.Project;
import org.argouml.kernel.ProjectManager;
import org.argouml.model.Model;
import org.argouml.ui.targetmanager.TargetManager;
import org.tigris.gef.presentation.Fig;
import org.tigris.gef.presentation.Handle;

/**
 * 
 * @author katharina
 */
public abstract class SelectionNodeClarifierWithAttributeButtons extends
        SelectionNodeClarifiers2 {

    /** Upper right corner Handle */
    protected static final int UPPER_RIGHT = 2;

    /** Lower right corner Handle */
    protected static final int LOWER_RIGHT = 7;

    private int localPressedButton;

    private static Icon addIcon = ResourceLoaderWrapper.lookupIconResource("Add");

    private Fig f;

    /**
     * @param f
     *            the given Fig
     */
    public SelectionNodeClarifierWithAttributeButtons(Fig f) {
       
        super(f);
        this.f = f;
    }

    @Override
    public void hitHandle(Rectangle cursor, Handle h) {
        super.hitHandle(cursor, h);

        int cx = getContent().getX();
        int cy = getContent().getY();
        int cw = getContent().getWidth();
        int ch = getContent().getHeight();

        // super returns -1 if we didn't hit any buttons, but maybe one of our
        // button was hit.
        if (h.index == -1) {
            if (hitBelow(cx + cw - (addIcon.getIconWidth() / 2), cy + 35,
                    addIcon.getIconWidth(), addIcon.getIconHeight(), cursor)) {
                h.index = UPPER_RIGHT;
            } else if (hitAbove(cx + cw - (addIcon.getIconWidth() / 2),
                    cy + ch, addIcon.getIconWidth(), addIcon.getIconHeight(),
                    cursor)) {
                h.index = LOWER_RIGHT;
            }
        }
    }

    public void mousePressed(MouseEvent me) {
        super.mousePressed(me);
        Handle h = new Handle(-1);
        hitHandle(me.getX(), me.getY(), 0, 0, h);

        // Get the index of the pressed button.
        localPressedButton = h.index;
    }

    @Override
    public void mouseReleased(MouseEvent me) {
        super.mouseReleased(me);

        // only handle our buttons
        if (localPressedButton != UPPER_RIGHT
                && localPressedButton != LOWER_RIGHT) {
            return;
        }

        Handle h = new Handle(-1);
        hitHandle(me.getX(), me.getY(), 0, 0, h);

        // see if mouse was released on the same button
        if (localPressedButton == h.index) {

            Object owner = getContent().getOwner();

            if (localPressedButton == UPPER_RIGHT) {
                onAttributeButtonClicked();
            }
            if (localPressedButton == LOWER_RIGHT) {
                onMethodButtonClicked(owner);
            }

            me.consume();
        }
    }

    private void onMethodButtonClicked(Object owner) {
        // Insert here code to get the FigCompartment for the operations
        // and call the setEditOnRedraw(true) method
        // buildOperation will result in an event which will then
        // be picked up by the FigCompartment and the new item
        // should be editable

        Model.getCoreFactory().buildOperation(owner, null);
    }

    private void onAttributeButtonClicked() {

        Object target = TargetManager.getInstance().getSingleModelTarget();
        Object classifier = null;

        if (Model.getFacade().isAClassifier(target)
                || Model.getFacade().isAAssociationEnd(target)) {
            classifier = target;
        } else if (Model.getFacade().isAFeature(target)) {
            classifier = Model.getFacade().getOwner(target);
        }

        Project project = ProjectManager.getManager().getCurrentProject();
        Object attrType = project.getDefaultAttributeType();
        Object attr = Model.getCoreFactory().buildAttribute2(classifier,
                attrType);

        TargetManager.getInstance().setTarget(attr);
        TargetManager.getInstance().getTarget();
    }

    @Override
    public void paintButtons(Graphics g) {

        super.paintButtons(g);

        int cx = getContent().getX();
        int cy = getContent().getY();
        int cw = getContent().getWidth();
        int ch = getContent().getHeight();

        paintButtonLeft(addIcon, g, cx + cw, cy + 35, UPPER_RIGHT);
        paintButtonLeft(addIcon, g, cx + cw, cy + ch - 10, LOWER_RIGHT);
    }
}
