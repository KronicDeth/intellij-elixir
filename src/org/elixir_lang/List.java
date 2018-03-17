package org.elixir_lang;


import com.ericsson.otp.erlang.OtpErlangList;
import com.ericsson.otp.erlang.OtpErlangObject;
import com.ericsson.otp.erlang.OtpErlangTuple;

import static org.elixir_lang.psi.impl.QuotableImpl.NIL;

/*
 * Java version of Elixir's List module, so that keyword pairs can be extracted from list.
 */
public class List {
    /**
     * Receives a list of tuples and returns the first tuple where the item at {@code position} in the tuple matches the
     * given {@code item}.
     *
     * @param tupleList List of tuples.  Tuples don't all have to be the same size or have position as a valid offset.
     * @param key key to search for at
     * @param position position of the
     * @return tuple with equal key or {@node NIL}.
     */
    public static OtpErlangObject keyfind(OtpErlangList tupleList, OtpErlangObject key, int position) {
      return keyfind(tupleList, key, position, NIL);
    }

    /**
     * Receives a list of tuples and returns the first tuple where the item at {@code position} in the tuple matches the
     * given {@code item}.
     *
     * @param tupleList List of tuples.  Tuples don't all have to be the same size or have position as a valid offset.
     * @param key key to search for at
     * @param position position of the
     * @param defaultValue
     * @return tuple with equal {@code key} or {@code defaultValue} if {@code key} is not found.
     */
    public static OtpErlangObject keyfind(OtpErlangList tupleList, OtpErlangObject key, int position, OtpErlangObject defaultValue) {
        OtpErlangObject matchTuple = defaultValue;

        for (OtpErlangObject element : tupleList) {
            OtpErlangTuple tuple = (OtpErlangTuple) element;

            if (tuple.elementAt(position).equals(key)) {
                matchTuple = tuple;
                break;
            }
        }

        return matchTuple;
    }
}
