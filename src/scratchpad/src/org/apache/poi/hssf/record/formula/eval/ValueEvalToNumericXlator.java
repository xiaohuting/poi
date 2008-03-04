/*
* Licensed to the Apache Software Foundation (ASF) under one or more
* contributor license agreements.  See the NOTICE file distributed with
* this work for additional information regarding copyright ownership.
* The ASF licenses this file to You under the Apache License, Version 2.0
* (the "License"); you may not use this file except in compliance with
* the License.  You may obtain a copy of the License at
*
*     http://www.apache.org/licenses/LICENSE-2.0
*
* Unless required by applicable law or agreed to in writing, software
* distributed under the License is distributed on an "AS IS" BASIS,
* WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
* See the License for the specific language governing permissions and
* limitations under the License.
*/
/*
 * Created on May 14, 2005
 *
 */
package org.apache.poi.hssf.record.formula.eval;

/**
 * @author Amol S. Deshmukh &lt; amolweb at ya hoo dot com &gt;
 *
 */
public final class ValueEvalToNumericXlator {

    public static final int STRING_IS_PARSED = 0x0001;
    public static final int BOOL_IS_PARSED = 0x0002;
    public static final int BLANK_IS_PARSED = 0x0004; // => blanks are not ignored, converted to 0
    
    public static final int REF_STRING_IS_PARSED = 0x0008;
    public static final int REF_BOOL_IS_PARSED = 0x0010;
    public static final int REF_BLANK_IS_PARSED = 0x0020;
    
    public static final int STRING_IS_INVALID_VALUE = 0x0800;
    
    private final int flags;
    
    
    public ValueEvalToNumericXlator(int flags) {
    	
    	if (false) { // uncomment to see who is using this class
			System.err.println(new Throwable().getStackTrace()[1].getClassName() + "\t0x"
					+ Integer.toHexString(flags).toUpperCase());
		}
		this.flags = flags;
    }
    
    /**
     * returned value can be either A NumericValueEval, BlankEval or ErrorEval.
     * The params can be either NumberEval, BoolEval, StringEval, or
     * RefEval
     * @param eval
     */
    public ValueEval attemptXlateToNumeric(ValueEval eval) {
        ValueEval retval = null;
        
        if (eval == null) {
            retval = BlankEval.INSTANCE;
        }
        
        // most common case - least worries :)
        else if (eval instanceof NumberEval) {
            retval = eval; 
        }
        
        // booleval
        else if (eval instanceof BoolEval) {
            retval = ((flags & BOOL_IS_PARSED) > 0)
                ? (NumericValueEval) eval
                : xlateBlankEval(BLANK_IS_PARSED);
        } 
        
        // stringeval 
        else if (eval instanceof StringEval) {
            retval = xlateStringEval((StringEval) eval); // TODO: recursive call needed
        }
        
        // refeval
        else if (eval instanceof RefEval) {
            retval = xlateRefEval((RefEval) eval);
        }
        
        // erroreval
        else if (eval instanceof ErrorEval) {
            retval = eval;
        }
        
        else if (eval instanceof BlankEval) {
            retval = xlateBlankEval(BLANK_IS_PARSED);
        }
        
        // probably AreaEval? then not acceptable.
        else {
            throw new RuntimeException("Invalid ValueEval type passed for conversion: " + eval.getClass());
        }
        
        return retval;
    }
    
    /**
     * no args are required since BlankEval has only one 
     * instance. If flag is set, a zero
     * valued numbereval is returned, else BlankEval.INSTANCE
     * is returned.
     */
    private ValueEval xlateBlankEval(int flag) {
        return ((flags & flag) > 0)
                ? (ValueEval) NumberEval.ZERO
                : BlankEval.INSTANCE;
    }
    
    /**
     * uses the relevant flags to decode the supplied RefVal
     * @param eval
     */
    private ValueEval xlateRefEval(RefEval reval) {
        ValueEval eval = reval.getInnerValueEval();
        
        // most common case - least worries :)
        if (eval instanceof NumberEval) {
            return eval;
        }
        
        if (eval instanceof BoolEval) {
            return ((flags & REF_BOOL_IS_PARSED) > 0)
                    ? (ValueEval) eval
                    : BlankEval.INSTANCE;
        } 
        
        if (eval instanceof StringEval) {
            return xlateRefStringEval((StringEval) eval);
        }
        
        if (eval instanceof ErrorEval) {
            return eval;
        }
        
        if (eval instanceof BlankEval) {
            return xlateBlankEval(REF_BLANK_IS_PARSED);
        }
        
        throw new RuntimeException("Invalid ValueEval type passed for conversion: ("
        		+ eval.getClass().getName() + ")");
    }
    
    /**
     * uses the relevant flags to decode the StringEval
     * @param eval
     */
    private ValueEval xlateStringEval(StringEval eval) {

        if ((flags & STRING_IS_PARSED) > 0) {
            String s = eval.getStringValue();
            Double d = OperandResolver.parseDouble(s);
            if(d == null) {
                return ErrorEval.VALUE_INVALID;
            }
            return new NumberEval(d.doubleValue());
        }
        // strings are errors?
        if ((flags & STRING_IS_INVALID_VALUE) > 0) {
            return ErrorEval.VALUE_INVALID;
        }
        
        // ignore strings
        return xlateBlankEval(BLANK_IS_PARSED);
    }
    
    /**
     * uses the relevant flags to decode the StringEval
     * @param eval
     */
    private ValueEval xlateRefStringEval(StringEval sve) {
        if ((flags & REF_STRING_IS_PARSED) > 0) {
            String s = sve.getStringValue();
            Double d = OperandResolver.parseDouble(s);
            if(d == null) {
                return ErrorEval.VALUE_INVALID;
            }
            return new NumberEval(d.doubleValue());
        }
        // strings are blanks
        return BlankEval.INSTANCE;
    }
}
