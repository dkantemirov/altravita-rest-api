import {View, React} from "../api"
import {Navigation} from "../components"

export class DefaultView extends View {
    render() {
        return (
            <div id="wrapper">

                {/*<!-- Navigation -->*/}
                <Navigation controller={this.controller}/>

                {/*!-- Page Content -->*/}
                <div id="page-wrapper">
                    <div className="container-fluid">
                        <div className="row">
                            <div className="col-lg-12">
                                <h1 className="page-header">Blank</h1>
                            </div>
                            {/*<!-- /.col-lg-12 -->*/}
                        </div>
                        {/*<!-- /.row -->*/}
                    </div>
                    {/*<!-- /.container-fluid -->*/}
                </div>
                {/*<!-- /#page-wrapper -->*/}

            </div>
        );
    }
}