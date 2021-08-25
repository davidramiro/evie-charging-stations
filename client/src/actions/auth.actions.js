export const USER_LOGIN = "USER_LOGIN";
export const USER_LOGOUT = "USER_LOGOUT";

export const logIn = (userParam) => {
  return (dispatch) => {
    dispatch({
      type: USER_LOGIN,
      payload: userParam,
    });
  };
};

export const logOut = () => {
  return (dispatch) => {
    dispatch({
      type: USER_LOGOUT,
    });
  };
};
