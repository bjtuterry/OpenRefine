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

package com.google.refine.grel.controls;

import java.util.regex.Pattern;

public class IsMobileNo extends IsTest {
    /**
     * 参见百度百科，截至2016年初的号段分配：
     * 130、131、132、133、134、135、136、137、138、139
     * 150、151、152、153、155、156、157、158、159
     * 173、176、177、178
     * 180、181、182、183、184、185、186、187、188、189
     * 171、1700、1701、1702、1705、1707、1708、1709
     * 
     * 145、147为上网卡号段
     */
    private static Pattern MOBILE = Pattern.compile("^((13\\d|14[57]|15[01235-9]|17[13678]|18\\d)\\d{8}|170[0125789]\\d{7})$");
    
    /**
     * 没有进行参数校验，请自行保证非空
     * @param mobile
     * @return
     */
    public static boolean isValid(String mobile)
    {
        return MOBILE.matcher(mobile).find();
    }
    
    @Override
    protected String getDescription() {
        return "Returns whether o is null";
    }

    @Override
    protected boolean test(Object o) {
        if (o == null) return false;
        String mobile = o instanceof String ? (String) o : o.toString();
        return isValid(mobile);
    }
}
