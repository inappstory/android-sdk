package com.inappstory.sdk.stories.ui.widgets;

import java.text.ParseException;
import java.util.ArrayList;

public class MaskedFormatter {

    // Potential values in mask.
    private static final char DIGIT_KEY = '−';
    private static final char LITERAL_KEY = '\'';
    private static final char UPPERCASE_KEY = 'U';
    private static final char LOWERCASE_KEY = 'L';
    private static final char ALPHA_NUMERIC_KEY = 'A';
    private static final char CHARACTER_KEY = '?';
    private static final char ANYTHING_KEY = '*';
    private static final char HEX_KEY = 'H';

    /**
     * The user specified mask.
     */
    private String mask;

    /**
     * Indicates if the value contains the literal characters.
     */
    private boolean containsLiteralChars;

    private static final MaskCharacter[] EmptyMaskChars =
            new MaskCharacter[0];

    /**
     * List of valid characters.
     */
    private String validCharacters;

    /**
     * List of invalid characters.
     */
    private String invalidCharacters;

    /**
     * String used to represent characters not present.
     */
    private char placeholder;

    /**
     * String used for the passed in value if it does not completely
     * fill the mask.
     */
    private String placeholderString;

    private transient MaskCharacter[] maskChars;


    /**
     * Indicates if the value being edited must match the mask.
     */
    @SuppressWarnings("unused")
    private boolean allowsInvalid;


    /**
     * Creates a MaskFormatter with no mask.
     */
    public MaskedFormatter() {
        setAllowsInvalid(false);
        containsLiteralChars = true;
        maskChars = EmptyMaskChars;
        placeholder = ' ';
    }


    public MaskedFormatter(String mask) throws ParseException {
        this();
        setMask(mask);
    }


    public void setMask(String mask) throws ParseException {
        this.mask = mask;
        updateInternalMask();
    }


    public String getMask() {
        return mask;
    }


    private void updateInternalMask() throws ParseException {
        String mask = getMask();
        ArrayList<MaskCharacter> fixed = new ArrayList<MaskCharacter>();
        ArrayList<MaskCharacter> temp = fixed;

        if (mask != null) {
            for (int counter = 0, maxCounter = mask.length();
                 counter < maxCounter; counter++) {
                char maskChar = mask.charAt(counter);

                switch (maskChar) {
                    case DIGIT_KEY:
                        temp.add(new DigitMaskCharacter());
                        break;
                    case LITERAL_KEY:
                        if (++counter < maxCounter) {
                            maskChar = mask.charAt(counter);
                            temp.add(new LiteralCharacter(maskChar));
                        }
                        // else: Could actually throw if else
                        break;
                    case UPPERCASE_KEY:
                        temp.add(new UpperCaseCharacter());
                        break;
                    case LOWERCASE_KEY:
                        temp.add(new LowerCaseCharacter());
                        break;
                    case ALPHA_NUMERIC_KEY:
                        temp.add(new AlphaNumericCharacter());
                        break;
                    case CHARACTER_KEY:
                        temp.add(new CharCharacter());
                        break;
                    case ANYTHING_KEY:
                        temp.add(new MaskCharacter());
                        break;
                    case HEX_KEY:
                        temp.add(new HexCharacter());
                        break;
                    default:
                        temp.add(new LiteralCharacter(maskChar));
                        break;
                }
            }
        }
        if (fixed.size() == 0) {
            maskChars = EmptyMaskChars;
        } else {
            maskChars = new MaskCharacter[fixed.size()];
            fixed.toArray(maskChars);
        }
    }


    public void setAllowsInvalid(boolean allowsInvalid) {
        this.allowsInvalid = allowsInvalid;
    }


    public void setValidCharacters(String validCharacters) {
        this.validCharacters = validCharacters;
    }


    public String getValidCharacters() {
        return validCharacters;
    }


    public void setInvalidCharacters(String invalidCharacters) {
        this.invalidCharacters = invalidCharacters;
    }

    /**
     * Returns the characters that are not valid for input.
     *
     * @return illegal characters.
     */
    public String getInvalidCharacters() {
        return invalidCharacters;
    }


    public void setValueContainsLiteralCharacters(
            boolean containsLiteralChars) {
        this.containsLiteralChars = containsLiteralChars;
    }


    public boolean getValueContainsLiteralCharacters() {
        return containsLiteralChars;
    }


    public void setPlaceholderCharacter(char placeholder) {
        this.placeholder = placeholder;
    }


    public char getPlaceholderCharacter() {
        return placeholder;
    }


    public void setPlaceholder(String placeholder) {
        this.placeholderString = placeholder;
    }


    public String getPlaceholder() {
        return placeholderString;
    }


    public String valueToString(Object value) throws ParseException {
        String sValue = (value == null) ? "" : value.toString();
        StringBuilder result = new StringBuilder();
        String placeholder = getPlaceholder();
        int[] valueCounter = {0};

        append(result, sValue, valueCounter, placeholder, maskChars);
        return result.toString();
    }


    private void append(StringBuilder result, String value, int[] index,
                        String placeholder, MaskCharacter[] mask)
            throws ParseException {
        for (int counter = 0, maxCounter = mask.length;
             counter < maxCounter; counter++) {
            mask[counter].append(result, value, index, placeholder);
        }
    }


    private class MaskCharacter {
        public boolean isLiteral() {
            return false;
        }

        public boolean isPhoneMaskCharacter(char character, int position) {
            return maskChars[position].isValidCharacter(character);
        }

        public boolean isValidCharacter(char character) {
            if (isLiteral()) {
                return getChar(character) == character;
            }

            character = getChar(character);

            String filter = getValidCharacters();
            if (filter != null && filter.indexOf(character) == -1) {
                return false;
            }

            filter = getInvalidCharacters();
            if (filter != null && filter.indexOf(character) != -1) {
                return false;
            }

            return true;
        }

        public char getChar(char character) {
            return character;
        }

        public boolean append(StringBuilder buffer, String formatting, int[] index, String placeholder) {
            boolean inString = index[0] < formatting.length();
            char character = inString ? formatting.charAt(index[0]) : 0;

            if (!inString) {
                return false;
            }

            if (isLiteral()) {
                buffer.append(getChar(character));

                if (inString && character == getChar(character)) {
                    index[0] = index[0] + 1;
                }
            } else if (index[0] >= formatting.length()) {
                if (placeholder != null && index[0] < placeholder.length()) {
                    buffer.append(placeholder.charAt(index[0]));
                } else {
                    buffer.append(getPlaceholderCharacter());
                }

                index[0] = index[0] + 1;
            } else if (isValidCharacter(character)) {
                buffer.append(getChar(character));
                index[0] = index[0] + 1;
            } else {
                return false;
            }

            return true;
        }
    }

    private class LiteralCharacter extends MaskCharacter {
        private char mLiteralCharacter;

        public LiteralCharacter(char character) {
            mLiteralCharacter = character;
        }

        public boolean isLiteral() {
            return true;
        }

        public char getChar(char aChar) {
            return mLiteralCharacter;
        }
    }

    private class DigitMaskCharacter extends MaskCharacter {
        public boolean isValidCharacter(char character) {
            return Character.isDigit(character) && super.isValidCharacter(character);
        }
    }

    private class UpperCaseCharacter extends MaskCharacter {
        public boolean isValidCharacter(char character) {
            return Character.isLetter(character) && super.isValidCharacter(character);
        }

        public char getChar(char character) {
            return Character.toUpperCase(character);
        }
    }

    private class LowerCaseCharacter extends MaskCharacter {
        public boolean isValidCharacter(char character) {
            return Character.isLetter(character) && super.isValidCharacter(character);
        }

        public char getChar(char character) {
            return Character.toLowerCase(character);
        }
    }

    private class AlphaNumericCharacter extends MaskCharacter {
        public boolean isValidCharacter(char character) {
            return Character.isLetterOrDigit(character) && super.isValidCharacter(character);
        }
    }

    private class CharCharacter extends MaskCharacter {
        public boolean isValidCharacter(char character) {
            return Character.isLetter(character) && super.isValidCharacter(character);
        }
    }

    private class HexCharacter extends MaskCharacter {
        private static final String HEX_CHARS = "0123456789abcedfABCDEF";

        public boolean isValidCharacter(char character) {
            return HEX_CHARS.indexOf(character) != -1 && super.isValidCharacter(character);
        }

        public char getChar(char character) {
            if (Character.isDigit(character)) {
                return character;
            }

            return Character.toUpperCase(character);
        }
    }
}