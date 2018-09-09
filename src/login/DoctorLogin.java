package login;

import DAO.DAODoctorLogin;
import java.io.UnsupportedEncodingException;
import java.security.NoSuchAlgorithmException;
import java.sql.SQLException;

public class DoctorLogin extends UserLogin implements IUserLogin {

    public DoctorLogin(String userCode, String password) throws NoSuchAlgorithmException, UnsupportedEncodingException {
        super(userCode, password);
    }

    public boolean login() throws ClassNotFoundException, SQLException, InstantiationException, IllegalAccessException {
        DAODoctorLogin dao = new DAODoctorLogin();

        int id = dao.getIdUser(this.userCode);

        if (id > 0) {
            this.userId = id;
        } else {
            return false;
        }

        String password = dao.getPassword();
        if (password == null) {
            return false;
        }

        if (password.equals(this.getHashedPassword())) {
            return true;
        } else {
            return false;
        }
    }
}
