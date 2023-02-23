package application;

import db.DB;
import db.DbException;
import db.DbIntegrityException;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Statement;

public class Program {
    public static void main(String[] args) {
        Connection conn = null;
        Statement st = null;

        try {
            conn = DB.getConnection();
            conn.setAutoCommit(false);//Não é para confirmar as operações automaticamente. Deve ficar pendente.
            st = conn.createStatement();

            int rows1 = st.executeUpdate("update seller set BaseSalary = 2090 where DepartmentId = 1");

            //As linhas abaixo criam um erro e lançam uma exceção para entre duas operações para testar a integridade da transação
//            int x = 1;
//            if (x < 2) {
//                throw new SQLException("Fake error");
//            }

            int rows2 = st.executeUpdate("update seller set BaseSalary = 3090 where DepartmentId = 2");

            conn.commit();//Estou informando que a minha transação terminou. Ou seja, as linhas acima estão protegidas.

            System.out.println("rows1 = " + rows1);
            System.out.println("rows2 = " + rows2);

        } catch (SQLException ex) {
            try {
                conn.rollback();//retorna a situação anterior do banco
                throw new DbException("Transaction rolled back! Caused by: " + ex.getMessage());
            } catch (SQLException e) { //deu erro no rollback
                throw new DbException("Error trying to rollback! Caused by: " + e.getMessage());
            }
        } finally {
            DB.closeStatement(st);
            DB.closeConnection();
        }
    }
}
