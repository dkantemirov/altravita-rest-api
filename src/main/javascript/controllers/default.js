import {Controller} from "../api"
import {DefaultView} from "../views"
import {SignOut} from "./auth"

export class DefaultController extends SignOut(Controller) {
    constructor(application) {
        super(application, DefaultView);
    }
}