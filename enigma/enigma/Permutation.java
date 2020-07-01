package enigma;

import static enigma.EnigmaException.*;

/** Represents a permutation of a range of integers starting at 0 corresponding
 *  to the characters of an alphabet.
 *  @author Seongho Lee
 */
class Permutation {

    /** Set this Permutation to that specified by CYCLES, a string in the
     *  form "(cccc) (cc) ..." where the c's are characters in ALPHABET, which
     *  is interpreted as a permutation in cycle notation.  Characters in the
     *  alphabet that are not included in any cycle map to themselves.
     *  Whitespace is ignored. */
    Permutation(String cycles, Alphabet alphabet) {
        _alphabet = alphabet;
        cycles = cycles.replace("(", " ");
        cycles = cycles.trim();
        cycles = cycles.replace(")", " ");
        _cycles = cycles.split(" ");
    }

    /** Add the cycle c0->c1->...->cm->c0 to the permutation, where CYCLE is
     *  c0c1...cm. */
    public void addCycle(String cycle) {
        String[] newCycle = new String[_cycles.length + 1];
        for (int i = 0; i < _cycles.length; i++) {
            newCycle[i] = _cycles[i];
        }
        newCycle[_cycles.length + 1] = cycle;
        _cycles = newCycle;
        // FIXME
    }

    /** Return the value of P modulo the size of this permutation. */
    final int wrap(int p) {
        int r = p % size();
        if (r < 0) {
            r += size();
        }
        return r;
    }

    /** Returns the size of the alphabet I permute. */
    int size() {
        return _alphabet.size();
    }

    /** Return the result of applying this permutation to P modulo the
     *  alphabet size. */
    int permute(int p) {
        p = wrap(p);
        char c = _alphabet.toChar(p);
        char permuted = 0;
        boolean check = false;
        for (String str : _cycles) {
            for (int i = 0; i < str.length(); i++) {
                int size = str.length();
                if (c == str.charAt(i)) {
                    int adjust = i + 1;
                    if (i + 1 > size - 1) {
                        adjust = (i + 1) % size;
                    }
                    permuted = str.charAt(adjust);
                    check = true;
                }
            }
        }
        if (!check) {
            return p;
        }
        return _alphabet.toInt(permuted);
    }

    /** Return the result of applying the inverse of this permutation
     *  to  C modulo the alphabet size. */
    int invert(int c) {
        c = wrap(c);
        char cc = _alphabet.toChar(c);
        char permuted = 0;
        boolean check = false;
        for (String str : _cycles) {
            for (int i = 0; i < str.length(); i++) {
                if (cc == str.charAt(i)) {
                    int adjust = i - 1;
                    if (adjust < 0) {
                        adjust += str.length();
                    }
                    permuted = str.charAt(adjust);
                    check = true;
                }
            }
        }
        if (!check) {
            return c;
        }
        return _alphabet.toInt(permuted);
    }

    /** Return the result of applying this permutation to the index of P
     *  in ALPHABET, and converting the result to a character of ALPHABET. */
    char permute(char p) {
        return _alphabet.toChar(permute(_alphabet.toInt(p)));
    }

    /** Return the result of applying the inverse of this permutation to C. */
    char invert(char c) {
        return _alphabet.toChar(invert(_alphabet.toInt(c)));
    }

    /** Return the alphabet used to initialize this Permutation. */
    Alphabet alphabet() {
        return _alphabet;
    }

    /** Return true iff this permutation is a derangement (i.e., a
     *  permutation for which no value maps to itself). */
    boolean derangement() {
        int size = 0;
        for (String string : _cycles) {
            size += string.length();
        }
        if (size < _alphabet.size()) {
            return false;
        }
        for (String str : _cycles) {
            if (str.length() == 1) {
                return false;
            }
        }
        return true;
    }

    /** Alphabet of this permutation. */
    private Alphabet _alphabet;
    private String[] _cycles;
}
