import Map from "./components/Map";
import "./App.css";
import Authentication from "./components/Authentication";
import Administration from "./components/Administration";
import { Route, Switch } from "react-router-dom";

function App() {
  return (
    <div className="fullscreen">
      <Switch>
        <Route
          path="/admin"
          component={(props) => <Administration {...props} />}
        />
        <Route
          path="/auth"
          component={(props) => (
            <>
              <img id="watermark" src="images/brand-logo.png" alt="logo" />
              <Authentication {...props} />
            </>
          )}
        />
        <Route
          path="/map"
          component={(props) => (
            <>
              <img id="watermark" src="images/brand-logo.png" alt="logo" />
              <Map {...props} />
            </>
          )}
        />
        <Route
          path="/"
          component={(props) => (
            <>
              <img id="watermark" src="images/brand-logo.png" alt="logo" />
              <Map {...props} />
            </>
          )}
        />
      </Switch>
    </div>
  );
}

export default App;
