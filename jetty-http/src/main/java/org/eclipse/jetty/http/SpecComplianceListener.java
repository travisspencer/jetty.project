//
//  ========================================================================
//  Copyright (c) 1995-2018 Mort Bay Consulting Pty. Ltd.
//  ------------------------------------------------------------------------
//  All rights reserved. This program and the accompanying materials
//  are made available under the terms of the Eclipse Public License v1.0
//  and Apache License v2.0 which accompanies this distribution.
//
//      The Eclipse Public License is available at
//      http://www.eclipse.org/legal/epl-v10.html
//
//      The Apache License v2.0 is available at
//      http://www.opensource.org/licenses/apache2.0.php
//
//  You may elect to redistribute this code under either of these licenses.
//  ========================================================================
//

package org.eclipse.jetty.http;

/**
 * Listener for Spec Compliance Violations
 *
 * @see HttpCompliance
 * @see CookieCompliance
 * @see MultiPartFormDataCompliance
 */
public interface SpecComplianceListener
{
    /**
     * The reference to a specific place in the Spec
     */
    interface SpecReference
    {
        /**
         * The unique name (to Jetty) for this specific reference
         * @return the unique name for this reference
         */
        String getName();

        /**
         * The URL to the spec (and section, if possible)
         * @return the url to the spec
         */
        String getUrl();

        /**
         * The spec description
         * @return the description of the spec detail that this reference is about
         */
        String getDescription();

    }

    /**
     * Called when a violation in spec has been detected.
     *
     * @param specReference the reference to the spec being violated
     * @param details the detail of the violation
     */
    void onSpecComplianceViolation(SpecReference specReference, String details);
}
