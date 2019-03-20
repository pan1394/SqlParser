/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (c) 1997-2017 Oracle and/or its affiliates. All rights reserved.
 *
 * The contents of this file are subject to the terms of either the GNU
 * General Public License Version 2 only ("GPL") or the Common Development
 * and Distribution License("CDDL") (collectively, the "License").  You
 * may not use this file except in compliance with the License.  You can
 * obtain a copy of the License at
 * https://oss.oracle.com/licenses/CDDL+GPL-1.1
 * or LICENSE.txt.  See the License for the specific
 * language governing permissions and limitations under the License.
 *
 * When distributing the software, include this License Header Notice in each
 * file and include the License file at LICENSE.txt.
 *
 * GPL Classpath Exception:
 * Oracle designates this particular file as subject to the "Classpath"
 * exception as provided by Oracle in the GPL Version 2 section of the License
 * file that accompanied this code.
 *
 * Modifications:
 * If applicable, add the following below the License Header, with the fields
 * enclosed by brackets [] replaced by your own identifying information:
 * "Portions Copyright [year] [name of copyright owner]"
 *
 * Contributor(s):
 * If you wish your version of this file to be governed by only the CDDL or
 * only the GPL Version 2, indicate your decision by adding "[Contributor]
 * elects to include this software in this distribution under the [CDDL or GPL
 * Version 2] license."  If you don't indicate a single choice of license, a
 * recipient has the option to distribute your version of this file under
 * either the CDDL, the GPL Version 2 or to extend the choice of license to
 * its licensees as provided above.  However, if you add GPL Version 2 code
 * and therefore, elected the GPL Version 2 license, then the option applies
 * only if the new code is made subject to such option by the copyright
 * holder.
 */

package com.sun.codemodel;

import java.util.ArrayList;
import java.util.List;

/**
 * array creation and initialization.
 */
public final class JArray extends JExpressionImpl {

	private final JType type;
	private final JExpression size;
	private List<JExpression> exprs = null;
	private boolean titled = true;

	/**
	 * Add an element to the array initializer
	 */
	public JArray add(JExpression e) {
		if (exprs == null)
			exprs = new ArrayList<JExpression>();
		exprs.add(e);
		return this;
	}

	JArray(JType type, JExpression size) {
		this.type = type;
		this.size = size;
	}

	public boolean isTitled() {
		return titled;
	}

	public void setTitled(boolean titled) {
		this.titled = titled;
	}

	public void generate(JFormatter f) {

		// generally we produce new T[x], but when T is an array type (T=T'[])
		// then new T'[][x] is wrong. It has to be new T'[x][].
		int arrayCount = 0;
		JType t = type;

		while (t.isArray()) {
			t = t.elementType();
			arrayCount++;
		}
		if (this.titled) {

			f.p("new").g(t).p('[');
			if (size != null)
				f.g(size);
			f.p(']');

			for (int i = 0; i < arrayCount; i++)
				f.p("[]");
		}

		if ((size == null) || (exprs != null))
			f.p('{');
		if (exprs != null) {
			f.g(exprs);
		} else {
			f.p(' ');
		}
		if ((size == null) || (exprs != null))
			f.p('}');
	}

}
