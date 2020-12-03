package com.inappstory.sdk.stories.ui.widgets;

import android.text.Editable;
import android.text.TextWatcher;

import java.text.ParseException;
import java.util.regex.Pattern;

public class MaskedWatcher implements TextWatcher {

    private String mMask;
    public String prefix = "";
    String mResult = "";

    public MaskedWatcher(String mask, String prefix) {
        mMask = mask;
        this.prefix = prefix;
    }

    @Override
    public void afterTextChanged(Editable s) {

        String mask = mMask;
        String value = s.toString();

        if (value.equals(mResult))
            return;

        try {

            // prepare the formatter
            MaskedFormatter formatter = new MaskedFormatter(mask);
            formatter.setValueContainsLiteralCharacters(false);
            formatter.setPlaceholderCharacter((char) 1);

            // get a string with applied mask and placeholder chars
            value = formatter.valueToString(value);

            try {

                // find first placeholder
                value = value.substring(0, value.indexOf((char) 1));


                //process a mask char
                if (value.charAt(value.length() - 1) ==
                        mask.charAt(value.length() - 1)) {
                    value = value.substring(0, value.length() - 1);
                }

            } catch (Exception e) {
                e.printStackTrace();
            }
            boolean reset = false;
            for (int i = 0; i < value.length(); i++) {
                char c = value.charAt(i);
                char m = mMask.charAt(i);
                if (m == '_' && !Character.isDigit(c)) {
                    reset = true;
                }
                if (m != '_' && m != c) {
                    reset = true;
                }
            }
            mResult = value;
           /* if (!prefix.isEmpty() && !value.startsWith(prefix)) {
                String cleanString;
                String deletedPrefix = prefix.substring(0, prefix.length() - 1);
                if (value.startsWith(deletedPrefix)) {
                    cleanString = value.replaceAll(Pattern.quote(deletedPrefix), "");
                } else {
                    cleanString = value.replaceAll(Pattern.quote(prefix), "");
                }
                value = (prefix + cleanString);
            }*/
            s.replace(0, s.length(), reset ? lastString : value);


        } catch (ParseException e) {

            //the entered value does not match a mask
            int offset = e.getErrorOffset();
            value = removeCharAt(value, offset);
            s.replace(0, s.length(), value);

        }


    }

    String lastString = "";

    @Override
    public void beforeTextChanged(CharSequence s, int start, int count,
                                  int after) {
        lastString = s.toString();
    }

    @Override
    public void onTextChanged(CharSequence s, int start,
                              int before, int count) {
    }

    public static String removeCharAt(String s, int pos) {

        StringBuffer buffer = new StringBuffer(s.length() - 1);
        buffer.append(s.substring(0, pos)).append(s.substring(pos + 1));
        return buffer.toString();

    }
}