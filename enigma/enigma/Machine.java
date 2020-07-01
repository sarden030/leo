package enigma;


import java.util.ArrayList;
import java.util.HashMap;
import java.util.Collection;

import static enigma.EnigmaException.*;


/** Class that represents a complete enigma machine.
 *  @author Seongho Lee
 */

class Machine {


/** A new Enigma machine with alphabet ALPHA, 1 < NUMROTORS rotor slots,
 *  and 0 <= PAWLS < NUMROTORS pawls.  ALLROTORS contains all the
 *  available rotors. */

    Machine(Alphabet alpha, int numRotors, int pawls,
            Collection<Rotor> allRotors) {
        _alphabet = alpha;
        _numRotors = numRotors;
        _pawls = pawls;
        _allRotors = allRotors;
        _rotors = new ArrayList<>();
    }


/** Return the number of rotor slots I have. */

    int numRotors() {
        return _numRotors;
    }


/** Return the number pawls (and thus rotating rotors) I have. */

    int numPawls() {
        return _pawls;
    }


/** Set my rotor slots to the rotors named ROTORS from my set of
 *  available rotors (ROTORS[0] names the reflector).
 *  Initially, all rotors are set at their 0 setting. */

    void insertRotors(String[] rotors) {
        for (int i = 0; i < rotors.length; i++) {
            boolean check = false;
            for (Rotor rotor: _allRotors) {
                if (rotors[i].equals(rotor.name())) {
                    check = true;
                }
                if (rotors[0].equals(rotor.name())) {
                    Rotor reflector = rotor;
                }
            }
            if (!check) {
                throw error("wrong rotor");
            }
            for (String name : rotors) {
                for (Rotor rotor : _allRotors) {
                    if (name.equals(rotor.name())) {
                        _rotors.add(rotor);
                    }
                }
            }
        }
    }


/** Set my rotors according to SETTING, which must be a string of
 *  numRotors()-1 characters in my alphabet. The first letter refers
 *  to the leftmost rotor setting (not counting the reflector).  */

    void setRotors(String setting) {
        for (int i = 1; i < _numRotors; i++) {
            _rotors.get(i).set(setting.charAt(i - 1));
        }
    }

/** Set the plugboard to PLUGBOARD. */

    void setPlugboard(Permutation plugboard) {
        _plugborad = plugboard;
    }

/** Returns t result of converting the input character C (as an
 *  index in the range 0..alphabet size - 1), after first advancing

 *  the machine. */

    int convert(int c) {
        int conver = c;
        if (_plugborad != null) {
            conver = _plugborad.permute(conver);
        }
        boolean already = false;
        boolean[] check = new boolean[_numRotors];
        for(int i = 1; i < _numRotors; i = i + 1) {
            if (_rotors.get(i).atNotch() && _rotors.get(i - 1).rotates()) {
                check[i] = true;
                check[i - 1] = true;
            }
        }
        for (int r = _numRotors - 1; r > 0; r = r - 1) {
            if (_rotors.get(r).atNotch() && _rotors.get(r - 1).rotates()) {
                if (already) {
                    already = true;
                }
                else {
                    already = false;
                }
            }
        }
        check[_numRotors - 1] = true;

        for (int k = 0; k < _numRotors; k = k + 1 ) {
            if (check[k]) {
                _rotors.get(k).advance();
            }
        }


        for (int j = 1; j < _numRotors; j = j + 1) {
            conver = _rotors.get(_numRotors - j).convertForward(conver);
        }
        for (Rotor rotor : _rotors) {
            conver = rotor.convertBackward(conver);
        }
        if (_plugborad != null) {
            conver = _plugborad.invert(conver);
        }
        return conver;
    }


/** Returns the encoding/decoding of MSG, updating the state of
 *  the rotors accordingly. */

    String convert(String msg) {
        String converted = "";
        msg = msg.replace(" ", "");
        for (char c : msg.toCharArray()) {
            int convert = convert(_alphabet.toInt(c));
            converted += _alphabet.toChar(convert);
        }
        return converted;
    }


/** Common alphabet of my rotors. */

    private final Alphabet _alphabet;

/** _numRotors. */

    private int _numRotors;

/** _pawls. */

    private int _pawls;

/** _plugboard. */

    private Permutation _plugborad;

/** _allRotors. */

    private Collection<Rotor> _allRotors;

    /** ArrayList. */

    private ArrayList<Rotor> _rotors;

    /**
     *
     * @return getRotors.
     */
    public ArrayList<Rotor> getRotors() {
        return _rotors;
    }
}
