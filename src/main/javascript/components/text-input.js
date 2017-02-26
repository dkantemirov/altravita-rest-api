import {Component, React, BootstrapGridSystemType} from "../api"

export const TextInputType = {
    TEXT: Symbol("text"),
    PASSWORD: Symbol("password"),
    DATETIME: Symbol("datetime"),
    DATETIME_LOCAL: Symbol("datetime-local"),
    MONTH: Symbol("month"),
    TIME: Symbol("time"),
    WEEK: Symbol("week"),
    NUMBER: Symbol("number"),
    EMAIL: Symbol("email"),
    URL: Symbol("url"),
    SEARCH: Symbol("search"),
    TELEPHONE: Symbol("tel"),
    COLOR: Symbol("color")
};

export class TextInput extends Component {
    get value() {
        return this.refs.input.value;
    }

    render() {
        const {label, bootstrap, type: {value: type}, placeholder} = this.props;
        if (label) {
            const {type: {value: gridType}, col: {label: labelCol, input: inputCol}} = bootstrap.grid;
            return (
                <div className="form-group" ref="formGroup">
                    <label className={`col-${gridType}-${labelCol} control-label`}>{label}</label>
                    <div className={`col-${gridType}-${inputCol}`}>
                        <input ref="input" className="form-control" placeholder={placeholder} type={type}/>
                    </div>
                </div>
            );
        } else {
            return (
                <div className="form-group" ref="formGroup">
                    <input ref="input" className="form-control" placeholder={placeholder} type={type}/>
                </div>
            );
        }
    }
}

TextInput.defaultProps = {
    type: TextInputType.TEXT,
    placeholder: "",
    label: "",
    required: false,
    bootstrap: {
        grid: {
            type: BootstrapGridSystemType.SMALL_DEVICES,
            col: {
                label: 2,
                input: 10
            }
        }
    }
};

TextInput.propTypes = {
    type: React.PropTypes.symbol,
    placeholder: React.PropTypes.string,
    label: React.PropTypes.string,
    required: React.PropTypes.bool,
    bootstrap: React.PropTypes.shape({
        grid: React.PropTypes.shape({
            type: React.PropTypes.symbol,
            col: React.PropTypes.shape({
                label: React.PropTypes.number,
                input: React.PropTypes.number
            })
        })
    })
};
