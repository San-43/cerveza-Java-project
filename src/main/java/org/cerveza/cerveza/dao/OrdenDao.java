package org.cerveza.cerveza.dao;

import org.cerveza.cerveza.model.Orden;

import java.util.List;

public interface OrdenDao {
    void insert (Orden orden) throws Exception;
    void update (Orden orden) throws Exception;
    void delete (int id) throws Exception;
    List<Orden> findAll() throws Exception;

    // DTO for ComboBox display
    class IdName {
        public final int idPresentacion;
        public final String displayName;
        public IdName(int idPresentacion, String displayName) {
            this.idPresentacion = idPresentacion;
            this.displayName = displayName;
        }
        @Override
        public String toString() { return displayName; }
    }

    // Get presentaciones with full display name (marca + cerveza + envase)
    List<IdName> findPresentacionesWithFullName() throws Exception;
}
