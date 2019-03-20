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

import java.util.List;

/**
 * Catch block for a try/catch/finally statement
 */

public class JCatchBlock implements JGenerable {

	JClass exception;
	private JVar var = null;
	private JBlock body = new JBlock();
	private List<JClass> list = null;

	JCatchBlock(JClass exception) {
		this.exception = exception;
	}

	JCatchBlock(List<JClass> list) {
		this.list = list;
	}

	public JVar param(String name) {
		if (var != null)
			throw new IllegalStateException();
		var = new JVar(JMods.forVar(JMod.NONE), exception, name, null);
		return var;
	}

	public JBlock body() {
		return body;
	}

	public void generate(JFormatter f) {
		if (var == null) {
			var = new JVar(JMods.forVar(JMod.NONE), exception, "_x", null);
		}
		if (list != null) {
			JFormatter h = f.p("catch (");
			int i = 0;
			for (JClass jclass : list) {
				h = h.t(jclass);
				i++;
				if (i != list.size()) {
					h = h.p("|");
				}
			}
			h.p(var.name()).p(')').g(body);
		} else {

			f.p("catch (").b(var).p(')').g(body);
		}

	}

}
