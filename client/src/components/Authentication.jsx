import React, { Component } from "react";
import "../login.css";
import { bindActionCreators } from "redux";
import { connect } from "react-redux";
import { logIn } from "../actions/auth.actions";
import Emoji from "emoji-dictionary";
import { Toast } from "react-bootstrap";
import { Redirect } from "react-router-dom";

const userMap = new Map();

const provider = [
  "NovoFleet",
  "PlugSurfing",
  "RheinEnergie",
  "Naturstrom",
  "Volkswagen We Charge",
  "EnBW",
  "E.ON",
  "TankE",
  "allego",
  "IONITY",
  "EWE",
];

class Authentication extends Component {
  state = {
    user: "",
    password: "",
    loginError: false,
  };

  componentDidMount() {
    provider.forEach((prov) => {
      userMap.set(prov, "hello123");
    });
  }

  handleLogin = (ev) => {
    ev.preventDefault();
    if (userMap.has(this.state.username)) {
      if (userMap.get(this.state.username) === this.state.password) {
        this.props.logIn(this.state.username);
        return;
      }
    }
    this.setState({ loginError: true });
  };

  handleUsernameInput = (ev) => {
    this.setState({ username: ev.target.value });
  };

  handlePasswordInput = (ev) => {
    this.setState({ password: ev.target.value });
  };

  render() {
    if (this.props.auth.status) {
      return <Redirect to="/admin" />;
    }
    return (
      <div className="cotainer mt-5">
        <div className="row justify-content-center mr-3 ml-3">
          <div className="col-md-6 mx-auto mt-5">
            <div className="card">
              <div className="card-header">
                <h5>Provider Login</h5>
              </div>
              <div className="card-body">
                <form onSubmit={this.handleLogin}>
                  <div className="form-group row">
                    <label
                      htmlFor="username"
                      className="col-md-4 col-form-label text-md-right"
                    >
                      Provider ID
                    </label>
                    <div className="col-md-6">
                      <input
                        type="text"
                        id="username"
                        className="form-control"
                        name="username"
                        onChange={this.handleUsernameInput}
                        required
                        autoFocus
                      ></input>
                    </div>
                  </div>

                  <div className="form-group row">
                    <label
                      htmlFor="password"
                      className="col-md-4 col-form-label text-md-right"
                    >
                      Password
                    </label>
                    <div className="col-md-6">
                      <input
                        type="password"
                        id="password"
                        className="form-control"
                        onChange={this.handlePasswordInput}
                        name="password"
                        required
                      ></input>
                    </div>
                  </div>
                  <button
                    className="btn btn-primary btn-lg float-right"
                    type="submit"
                    color="primary"
                  >
                    Login
                  </button>
                </form>
                <Toast
                  animation={true}
                  onClose={() => this.setState({ loginError: false })}
                  show={this.state.loginError}
                  autohide
                  delay={300000}
                >
                  <Toast.Header>
                    <img
                      src="holder.js/20x20?text=%20"
                      className="rounded mr-2"
                      alt=""
                    />
                    <strong className="mr-auto">
                      Invalid Credentials!{" "}
                      {Emoji.getUnicode(Emoji.getName("🤨"))}
                    </strong>
                  </Toast.Header>
                  <Toast.Body>
                    Please check your username and password.
                  </Toast.Body>
                </Toast>
              </div>
            </div>
          </div>
        </div>
      </div>
    );
  }
}

const mapStateToProps = (state) => ({
  auth: state.auth,
});

const mapDispatchToProps = (dispatch) =>
  bindActionCreators(
    {
      logIn: logIn,
    },
    dispatch
  );

export default connect(mapStateToProps, mapDispatchToProps)(Authentication);
