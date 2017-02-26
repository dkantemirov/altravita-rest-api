import {Component, React} from "../api"

export class Spinner extends Component {
    render() {
        return (
            <button className="btn btn-lg btn-warning center-block">
                <span className="glyphicon glyphicon-refresh glyphicon-refresh-animate"/> Loading...
            </button>
        )
    }
}
