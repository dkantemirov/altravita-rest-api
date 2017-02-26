import {View, React} from "../api"
import {Alert, AlertType, Spinner, TextInput, TextInputType} from "../components"

export class AuthView extends View {
    constructor(props) {
        super(props);
        this.defaultState();
    }

    defaultState() {
        this.state = {
            rejected: false,
            rejectMessage: "",
            waiting: false
        };
    }

    toState(waiting, rejected, rejectMessage) {
        this.setState({
            waiting,
            rejected,
            rejectMessage
        });
    };

    signInHandler(event) {
        const {refs: {username: {value: username}, password: {value: password}, remember: {checked: remember}}, controller} = this;
        const rejected = ({rejected, message}) => {
            if (rejected) this.toState(false, true, message)
        };
        event.preventDefault();
        this.toState(true, false, "");
        controller.signIn(username, password, remember)
            .then(rejected)
    };

    render() {
        const {state: {rejectMessage: message, rejected, waiting}} = this;
        return (
            <div className="container">
                <div className="row">
                    <div className="col-md-4 col-md-offset-4">
                        <div className="login-panel panel panel-default">
                            <div className="panel-heading">
                                <h3 className="panel-title">Войдите в приложение</h3>
                            </div>
                            <div className="panel-body">
                                {rejected ? <Alert message={message} type={AlertType.DANGER}/> : null}
                                {
                                    waiting ?
                                        <Spinner/> :
                                        <form role="form" onSubmit={event => this.signInHandler(event)}>
                                            <fieldset>
                                                <TextInput ref="username" placeholder="Логин"
                                                           type={TextInputType.TEXT}/>
                                                <TextInput ref="password" placeholder="Пароль"
                                                           type={TextInputType.PASSWORD}/>
                                                <div className="checkbox">
                                                    <label>
                                                        <input id="remember" ref="remember" name="remember"
                                                               type="checkbox"
                                                               defaultChecked="true"/>Запомни меня
                                                    </label>
                                                </div>
                                                <button id="login" type="submit"
                                                        className="btn btn-lg btn-success btn-block">Войти
                                                </button>
                                            </fieldset>
                                        </form>
                                }
                            </div>
                        </div>
                    </div>
                </div>
            </div>
        );
    }
}