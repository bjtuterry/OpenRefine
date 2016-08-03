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

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class IsIDCardNo extends IsTest {

    final static Logger logger = LoggerFactory.getLogger(IsIDCardNo.class);
    private static Pattern IDNO_18 = Pattern.compile(
            "^((1[1-5])|(2[1-3])|(3[1-7])|(4[1-6])|(5[0-4])|(6[1-5])|71|(8[12])|91)\\d{4}((19\\d{2}(0[13-9]|1[012])(0[1-9]|[12]\\d|30))|(19\\d{2}(0[13578]|1[02])31)|(19\\d{2}02(0[1-9]|1\\d|2[0-8]))|(19([13579][26]|[2468][048]|0[48])0229)|(20[0-1]\\d{5}))\\d{3}(\\d|X|x)$");
    private static Pattern IDNO_15 = Pattern.compile(
            "^((1[1-5])|(2[1-3])|(3[1-7])|(4[1-6])|(5[0-4])|(6[1-5])|71|(8[12])|91)\\d{4}((\\d{2}(0[13-9]|1[012])(0[1-9]|[12]\\d|30))|(\\d{2}(0[13578]|1[02])31)|(\\d{2}02(0[1-9]|1\\d|2[0-8]))|(([13579][26]|[2468][048]|0[48])0229))\\d{3}$");

    private static int[] WEIGHT = { 7, 9, 10, 5, 8, 4, 2, 1, 6, 3, 7, 9, 10, 5, 8, 4, 2 };
    private static char[] CHECKSUM = { '1', '0', 'X', '9', '8', '7', '6', '5', '4', '3', '2' };

    /**
     * 方法未检测参数的合法性，调用方需自行保证
     * 
     * @param id
     * @return
     */
    public static char getValidateCode(String id) {
        int sum = 0;
        int mode = 0;
        for (int i = 0; i < 17; i++) {
            sum = sum + Integer.parseInt(String.valueOf(id.charAt(i))) * WEIGHT[i];
        }
        mode = sum % 11;
        return CHECKSUM[mode];
    }

    /**
     * 未做参数检查，自行确保合法性
     * 
     * @param id
     *            can not be null.
     * @return
     */
    public static boolean isValidIDCardNo_15(String id) {
        return IDNO_15.matcher(id).find();
    }

    /**
     *
     * 
     * @param id
     * @return
     */
    public static boolean isValid(String id) {
        if (id == null) {
            return false;
        }

        boolean valid = false;
        // check 18 digits
        Matcher m = IDNO_18.matcher(id);
        if (m.find()) {
            char lastChar = Character.toUpperCase(id.charAt(17));
            char checksum = getValidateCode(id);

            valid = (lastChar == checksum);
            // logger.info(lastChar +"\t" + checksum + "\t" + valid);
        }

        // check 15 digits
        if (!valid) {
            m = IDNO_15.matcher(id);
            valid = m.find();
        }

        return valid;
    }

    @Override
    protected String getDescription() {
        return "Returns whether o is a valid identity card number";
    }

    @Override
    protected boolean test(Object o) {
        if (o == null) return false;
        String id = o instanceof String ? (String) o : o.toString();
        return isValid(id);

    }
}
