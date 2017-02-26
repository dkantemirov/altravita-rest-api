import React from "react"

export class View extends React.Component {
    constructor(props) {
        super(props);
        const self = this;
        self.controller = props.controller;
        if (props.model) self.model = props.model;
    }
}