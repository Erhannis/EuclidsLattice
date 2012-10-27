/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package latticetestwork;

import java.io.DataOutputStream;
import java.io.IOException;

/**
 * <b>Implement the following:</b><br>
 * public static Object fromBytes(DataInputStream dis);<br>
 * <br>
 * I have a suspicions that this is like Serializable, but that one doesn't seem
 * to DO anything.  This one provides a method for turning the thing into bytes,
 * and a constructor for making one from a stream.<br>
 * ...<br>
 * Why the garbage can you put neither constructors nor static methods in an interface?</br>
 * ...<br>
 * Argh, nor an abstract!  What gives, Java?!  Ok, I'm just going to freaking use
 * reflection to call "fromBytes" on a Streamable, convention be hanged.
 * @author mewer12
 */
public interface Streamable {
    public void toBytes(DataOutputStream dos) throws IOException ;
}
