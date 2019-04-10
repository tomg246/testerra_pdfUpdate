/*
 * Created on 03.13.2014
 *
 * Copyright(c) 2011 - 2014 T-Systems Multimedia Solutions GmbH
 * Riesaer Str. 5, 01129 Dresden
 * All rights reserved.
 */
package eu.tsystems.mms.tic.testframework.layout.matching.error;

import eu.tsystems.mms.tic.testframework.layout.matching.graph.Node;
import eu.tsystems.mms.tic.testframework.layout.reporting.GraphicalReporter;
import eu.tsystems.mms.tic.testframework.report.model.Serial;

import java.io.Serializable;

/**
 * User: rnhb
 * Date: 21.05.14
 */
public abstract class LayoutFeature implements Serializable {

    private static final long serialVersionUID = Serial.SERIAL;

    protected Node nodeOfElement;
    protected String name;

    protected LayoutFeature(Node nodeOfElement) {
        this.nodeOfElement = nodeOfElement;
        name = this.getClass().getSimpleName();
    }

    public abstract void callbackDraw(GraphicalReporter graphicalReporter);

    public String getName() {
        return name;
    }
}