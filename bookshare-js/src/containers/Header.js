import React from 'react';
import { Link } from "react-router-dom";
import { LoginBar } from "./LoginBar";
import { BooksBar } from "./BooksBar";

export default class Header extends React.Component {
    render() {
        return (
          <nav className="navbar navbar-expand-lg navbar-light bg-light">
              <div id="navbarNavDropdown" className="navbar-collapse">
                  <Link className="navbar-brand" to="/">Navbar</Link>
                      <BooksBar />
                      <LoginBar />
              </div>
          </nav>
          )
    }
}
