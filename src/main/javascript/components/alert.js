import {Component, React} from "../api"

export const AlertType = {
    SUCCESS: Symbol("alert-success"),
    INFO: Symbol("alert-info"),
    WARNING: Symbol("alert-warning"),
    DANGER: Symbol("alert-danger")
};

export class Alert extends Component {
    render() {
        const {type: t, message: m} = this.props;
        const cssClass = t.value;
        return <div className={`alert ${cssClass}`}>{m}</div>;
    }
}

Alert.defaultProps = {
    type: AlertType.INFO,
    message: "Alert message..."
};

Alert.propTypes = {
    type: React.PropTypes.symbol,
    message: React.PropTypes.string
};
