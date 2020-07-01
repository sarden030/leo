package enigma;

import java.io.File;
import java.io.IOException;
import java.io.PrintStream;

import java.util.ArrayList;
import java.util.NoSuchElementException;
import java.util.Scanner;

import static enigma.EnigmaException.*;

/** Enigma simulator.
 *  @author Seongho Lee
 */
public final class Main {

    /** Process a sequence of encryptions and decryptions, as
     *  specified by ARGS, where 1 <= ARGS.length <= 3.
     *  ARGS[0] is the name of a configuration file.
     *  ARGS[1] is optional; when present, it names an input file
     *  containing messages.  Otherwise, input comes from the standard
     *  input.  ARGS[2] is optional; when present, it names an output
     *  file for processed messages.  Otherwise, output goes to the
     *  standard output. Exits normally if there are no errors in the input;
     *  otherwise with code 1. */
    public static void main(String... args) {
        try {
            new Main(args).process();
            return;
        } catch (EnigmaException excp) {
            System.err.printf("Error: %s%n", excp.getMessage());
        }
        System.exit(1);
    }

    /** Check ARGS and open the necessary files (see comment on main). */
    Main(String[] args) {
        if (args.length < 1 || args.length > 3) {
            throw error("Only 1, 2, or 3 command-line arguments allowed");
        }

        _config = getInput(args[0]);

        if (args.length > 1) {
            _input = getInput(args[1]);
        } else {
            _input = new Scanner(System.in);
        }

        if (args.length > 2) {
            _output = getOutput(args[2]);
        } else {
            _output = System.out;
        }
    }

    /** Return a Scanner reading from the file named NAME. */
    private Scanner getInput(String name) {
        try {
            return new Scanner(new File(name));
        } catch (IOException excp) {
            throw error("could not open %s", name);
        }
    }

    /** Return a PrintStream writing to the file named NAME. */
    private PrintStream getOutput(String name) {
        try {
            return new PrintStream(new File(name));
        } catch (IOException excp) {
            throw error("could not open %s", name);
        }
    }

    /** Configure an Enigma machine from the contents of configuration
     *  file _config and apply it to the messages in _input, sending the
     *  results to _output. */
    private void process() {
        String s1, s2 = "";
        String decode;
        Machine machine = readConfig();
        boolean find = true;
        int count = 1;
        while (_input.hasNextLine()) {
            find = false;
            String settings = _input.nextLine();
            if (_input.hasNextLine()) {
                s1 = s2;
            } else {
                return;
            }
            if (settings.startsWith("*")) {
                if (count != 1) {
                    machine.getRotors().clear();
                }
                setUp(machine, settings);
            } else {
                printMessageLine(machine.convert(settings));
            }
            count++;
        }
        while (_input.hasNextLine()) {
            if (s2.startsWith("*")) {
                s1 = s2;
            }
        }
        if (find) {
            throw new EnigmaException("Put other input");
        }
    }

    /** Return an Enigma machine configured from the contents of configuration
     *  file _config. */
    private Machine readConfig() {
        try {
            int count = 0;
            _numRotors = _config.nextInt();
            _alphabet = new Alphabet(_config.nextLine());
            _allRotors = new ArrayList<>();
            _pawls = _config.nextInt();
            while (_config.hasNextLine() == true) {
                String attempt = _config.nextLine();
                if (attempt.length() == 0) {
                    attempt = _config.nextLine();
                }
                Scanner scanTemp = new Scanner(attempt);
                String temp2 = scanTemp.next();
                if (temp2.startsWith("(")) {
                    String cycles = temp2;
                    while (scanTemp.hasNext()) {
                        cycles += scanTemp.next();
                    }
                    _allRotors.get(count - 1).permutation().addCycle(cycles);

                } else {
                    _allRotors.add(readRotor(attempt));
                    count += 1;
                }
            }
            return new Machine(_alphabet, _numRotors, _pawls, _allRotors);
        } catch (NoSuchElementException excp) {
            throw error("cut configuration file");
        }
    }

    /** Return a rotor, reading its description from _config.
     * @param temp*/
    private Rotor readRotor(String temp) {
        try {
            String name = "", notches = "", cycles = "";
            char moving = 0;
            String x = temp;
            Scanner allow = new Scanner(x);
            name = allow.next();
            String tempppt = allow.next();
            notches = tempppt.substring(1);
            moving = tempppt.charAt(0);

            while (allow.hasNext()) {
                cycles = cycles + allow.next();
            }
            if (moving == 'M') {
                return new MovingRotor(name, new Permutation(cycles, _alphabet), notches);
            } else if (moving == 'R') {
                return new Reflector(name, new Permutation(cycles, _alphabet));
            } else{
                return new FixedRotor(name, new Permutation(cycles, _alphabet));
            }
        } catch (NoSuchElementException excp) {
            throw error("bad rotor description");
        }
    }

    /** Set M according to the specification given on SETTINGS,
     *  which must have the format specified in the assignment. */
    private void setUp(Machine M, String settings) {
        String[] Ary = settings.split("[ ]");
        String[] inserting = new String[_numRotors];
        Scanner scan = new Scanner(settings);
        int numRotors = M.numRotors();
        for(int i = 1; i < _numRotors + 1; i = i + 1){
            inserting[i - 1] = Ary[i];
            numRotors++;
        }
        _mainMachine = new Machine(_alphabet,
                M.numRotors(), M.numPawls(), _allRotors);
        _mainMachine.setRotors(scan.next());
        String setting = Ary[_numRotors + 1];
        String plugBoard = "";
        for(int i = _numRotors + 2; i < Ary.length; i= i + 1) {
            plugBoard += Ary[i];
        }
        M.insertRotors(inserting);
        M.setPlugboard(new Permutation(plugBoard, _alphabet));
        M.setRotors(setting);
    }

    /**
     * A function to find a length.
     */
    private void length() {
        int i = 0;
        while (_config.hasNextLine()) {
            _config.nextLine();
            i += 1;
        }
        _configlength = i;
    }

    /** Print MSG in groups of five (except that the last group may
     *  have fewer letters). */
    private void printMessageLine(String msg) {
        if (msg.equals("")) {
            _output.println();
        }
        for (int i = 0; i < msg.length(); i += 1) {
            if (i % 5 == 0 && i != 0) {
                _output.print(" ");
            }
            _output.print(msg.charAt(i));
            int dan = msg.length() - i;
        }
        _output.println();
    }

    /** Alphabet used in this machine. */
    private Alphabet _alphabet;

    /** Source of input messages. */
    private Scanner _input;

    /** Source of machine configuration. */
    private Scanner _config;

    /** File for encoded/decoded messages. */
    private PrintStream _output;

    /** _pawls. */
    private int _pawls;

    /** _numRotors. */
    private int _numRotors;

    /** The length of config. */
    private int _configlength;

    /** _allRotors. */
    private ArrayList<Rotor> _allRotors;

    /**
     * Type and notches of current rotor.
     */
    private Machine _mainMachine;
}
