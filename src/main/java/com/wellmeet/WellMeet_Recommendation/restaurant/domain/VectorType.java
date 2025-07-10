package com.wellmeet.WellMeet_Recommendation.restaurant.domain;

import org.hibernate.engine.spi.SharedSessionContractImplementor;
import org.hibernate.usertype.UserType;

import java.io.Serializable;
import java.sql.*;
import java.util.Arrays;

public class VectorType implements UserType<float[]> {

    @Override
    public int getSqlType() {
        return Types.OTHER;
    }

    @Override
    public Class<float[]> returnedClass() {
        return float[].class;
    }

    @Override
    public boolean equals(float[] x, float[] y) {
        if (x == y) return true;
        if (x == null || y == null) return false;
        return Arrays.equals(x, y);
    }

    @Override
    public int hashCode(float[] x) {
        return x != null ? Arrays.hashCode(x) : 0;
    }

    @Override
    public float[] nullSafeGet(ResultSet rs, int position,
                               SharedSessionContractImplementor session,
                               Object owner) throws SQLException {
        String value = rs.getString(position);
        if (rs.wasNull() || value == null) {
            return null;
        }

        // "[0.1,0.2,0.3]" 형태의 문자열을 float[]로 변환
        value = value.substring(1, value.length() - 1); // 대괄호 제거
        String[] parts = value.split(",");
        float[] result = new float[parts.length];
        for (int i = 0; i < parts.length; i++) {
            result[i] = Float.parseFloat(parts[i].trim());
        }
        return result;
    }

    @Override
    public void nullSafeSet(PreparedStatement st, float[] value,
                            int index, SharedSessionContractImplementor session)
            throws SQLException {
        if (value == null) {
            st.setNull(index, Types.OTHER);
        } else {
            // float[]를 PostgreSQL vector 형식 문자열로 변환
            StringBuilder sb = new StringBuilder("[");
            for (int i = 0; i < value.length; i++) {
                if (i > 0) sb.append(",");
                sb.append(value[i]);
            }
            sb.append("]");
            st.setObject(index, sb.toString(), Types.OTHER);
        }
    }

    @Override
    public float[] deepCopy(float[] value) {
        return value != null ? value.clone() : null;
    }

    @Override
    public boolean isMutable() {
        return true;
    }

    @Override
    public Serializable disassemble(float[] value) {
        return value != null ? value.clone() : null;
    }

    @Override
    public float[] assemble(Serializable cached, Object owner) {
        return cached != null ? (float[]) cached : null;
    }
}
