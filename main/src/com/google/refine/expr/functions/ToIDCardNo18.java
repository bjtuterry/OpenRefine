/*

Copyright 2010, Google Inc.
All rights reserved.

Redistribution and use in source and binary forms, with or without
modification, are permitted provided that the following conditions are
met:

    * Redistributions of source code must retain the above copyright
notice, this list of conditions and the following disclaimer.
    * Redistributions in binary form must reproduce the above
copyright notice, this list of conditions and the following disclaimer
in the documentation and/or other materials provided with the
distribution.
    * Neither the name of Google Inc. nor the names of its
contributors may be used to endorse or promote products derived from
this software without specific prior written permission.

THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS
"AS IS" AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT
LIMITED TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR
A PARTICULAR PURPOSE ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT
OWNER OR CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL,
SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT
LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE,           
DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY           
THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
(INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE
OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.

*/

package com.google.refine.expr.functions;

import java.util.Properties;

import org.json.JSONException;
import org.json.JSONWriter;

import com.google.refine.expr.EvalError;
import com.google.refine.grel.ControlFunctionRegistry;
import com.google.refine.grel.Function;
import com.google.refine.grel.controls.IsIDCardNo;

public class ToIDCardNo18 implements Function {

    @Override
    public Object call(Properties bindings, Object[] args) {
        if (args.length == 1) {
            Object v = args[0];
            
            if(v == null)
            {
                return new EvalError(ControlFunctionRegistry.getFunctionName(this) + " expects a non-null string");
            }
            
            String s = (v instanceof String ? (String) v : v.toString());
            if(!IsIDCardNo.isValidIDCardNo_15(s))
            {
                return new EvalError(ControlFunctionRegistry.getFunctionName(this) + " expects a legal 15-digits identity card number string");
            }
            
            StringBuffer buffer = new StringBuffer();
            for(int i=0; i<s.length(); i++)
            {
                buffer.append(s.charAt(i));
                //insert year '19' after 6th digit
                if(i == 5)
                {
                    buffer.append("19");
                }
            }
            buffer.append(IsIDCardNo.getValidateCode(buffer.toString()));
            return buffer.toString();
        }
        return new EvalError(ControlFunctionRegistry.getFunctionName(this) + " expects a string");
    }

    @Override
    public void write(JSONWriter writer, Properties options)
        throws JSONException {
    
        writer.object();
        writer.key("description"); writer.value("Upgrade 15-digits identity card number to 18-digits");
        writer.key("params"); writer.value("15-digits string o");
        writer.key("returns"); writer.value("18-digits string");
        writer.endObject();
    }
}
