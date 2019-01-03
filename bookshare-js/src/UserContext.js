import React from "react";

export const UserContext = React.createContext({
    email:"",
    setEmail: (email) => {},
});

