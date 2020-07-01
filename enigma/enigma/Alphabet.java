package enigma;

/** An alphabet of encodable characters.  Provides a mapping from characters
 *  to and from indices into the alphabet.
 *  @author Seongho Lee
 */
class Alphabet {

    /** A new alphabet containing CHARS.  Character number #k has index
     *  K (numbering from 0). No character may be duplicated. */
    Alphabet(String chars) { _chars = chars; }

    /** A default alphabet of all upper-case characters. */
    Alphabet() {
        this("ABCDEFGHIJKLMNOPQRSTUVWXYZ");
    }

    /** Returns the size of the alphabet. */
    int size() {
        return _chars.length();
    }

    /** Returns true if CH is in this alphabet. */
    boolean contains(char ch) { return _chars.contains(String.valueOf(ch)); }

    /** Returns character number INDEX in the alphabet, where
     *  0 <= INDEX < size(). */
    char toChar(int index) {
        if (index < size() && index >= 0) {
            return _chars.charAt(index);
        }
        throw new EnigmaException("out of range");
    }

    /** Returns the index of character CH which must be in
     *  the alphabet. This is the inverse of toChar(). */
    int toInt(char ch) {
        int result = -1;
        for (int i = 0; i < size(); i++) {
            if (_chars.charAt(i) == ch) {
                result = i;
            }
        }
        if (result == -1) {
            throw new EnigmaException("Does not exit in alphabet");
        }
        return result;
    }

    /** String _chars. */
    private String _chars;

}
