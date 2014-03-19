package org.bytesoft.openjtcc.supports;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.Serializable;

import org.bytesoft.openjtcc.supports.serialize.ObjectSerializer;

import com.caucho.hessian.io.HessianInput;
import com.caucho.hessian.io.HessianOutput;

public class ObjectSerializerImpl implements ObjectSerializer {

	@Override
	public byte[] serialize(Object var) throws IOException {
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		try {
			HessianOutput output = new HessianOutput(baos);
			output.writeObject(var);
		} catch (IOException ex) {
			throw ex;
		} finally {
			try {
				baos.close();
			} catch (Exception ex) {
			}
		}
		return baos.toByteArray();
	}

	@Override
	public Object deserialize(byte[] bytes) throws IOException, ClassNotFoundException {
		Serializable variable = null;
		ByteArrayInputStream bais = new ByteArrayInputStream(bytes);
		try {
			HessianInput input = new HessianInput(bais);
			variable = (Serializable) input.readObject();
		} catch (IOException ex) {
			throw ex;
		} finally {
			try {
				bais.close();
			} catch (Exception ex) {
			}
		}
		return variable;
	}

}
