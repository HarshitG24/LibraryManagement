import React, { useEffect } from "react";
import { useDispatch, useSelector } from "react-redux";
import { Link, useNavigate } from "react-router-dom";
import { Button, Form, Input, Radio } from 'antd';
import { toast, ToastContainer } from "react-toastify";
import { userLoginThunk } from "../../services/user-thunks";
import { clearLogin } from "../../reducers/user-reducer";
import "./Login.css";


const Login = () => {

    const dispatch = useDispatch();
    const navigate = useNavigate();

    const currentUser = useSelector(state => state.user.profile);
    const { lastAttempt, error } = useSelector(state => state.user);

    useEffect(() => {
        return () => error && dispatch(clearLogin())
    }, [])

    useEffect(() => {
        if (error && !currentUser) {
            toast.error(`Login failed! User not found!`, {
                position: "bottom-right",
                autoClose: 500,
                hideProgressBar: true,
                closeOnClick: true,
                pauseOnHover: false,
                draggable: false,
                progress: undefined,
                theme: "colored",
            });
        } else if (currentUser) {
            toast.success("Login successful!", {
                position: "bottom-right",
                autoClose: 200,
                hideProgressBar: true,
                closeOnClick: true,
                pauseOnHover: false,
                draggable: false,
                progress: undefined,
                theme: "colored",
            });

            navigate('/');
        }
    }, [error, currentUser, lastAttempt])

    const onFinish = async (values) => {
        console.log('Login Attempted:', values)
        const { username, password } = values
        dispatch(userLoginThunk({ username, password }))

    }

    const onFinishFailed = (errorInfo) => {
        console.log('Login Attempt Failed:', errorInfo)
        toast.error("Error in username/password!", {
            position: "bottom-right",
            autoClose: 200,
            hideProgressBar: true,
            closeOnClick: true,
            pauseOnHover: false,
            draggable: false,
            progress: undefined,
            theme: "colored",
        });
    }

    return (

        <div className="background-radial-gradient overflow-hidden">
            <div className="px-4 py-5 px-md-5 text-center text-lg-start my-5">
                <div className="row gx-lg-5 justify-content-center mb-5">
                    <div className="col-lg-6 mb-5 mb-lg-0 position-relative">
                        <div id="radius-shape-1" className="position-absolute rounded-circle shadow-5-strong" />
                        <div id="radius-shape-2" className="position-absolute shadow-5-strong" />

                        <div className="card bg-glass">
                            <div className="card-body px-4 py-5 px-md-5 d-flex flex-column justify-content-center">
                                <h1>Login</h1>

                                <Form
                                    name="login"
                                    style={{ maxWidth: 600 }}
                                    initialValues={{ usertype: 'buyer' }}
                                    onFinish={onFinish}
                                    onFinishFailed={onFinishFailed}
                                >
                                    <Form.Item
                                        label="Username"
                                        name="username"
                                        rules={[{ required: true, message: 'Please input your username!' }]}
                                    >
                                        <Input />
                                    </Form.Item>

                                    <Form.Item
                                        label="Password"
                                        name="password"
                                        rules={[
                                            { required: true, message: 'Please input your password!' },
                                            { min: 6, message: 'Password must contain minimum 6 characters!' }
                                        ]}
                                    >
                                        <Input.Password />
                                    </Form.Item>

                                    <Form.Item wrapperCol={{ offset: 4, span: 16 }}>
                                        <Button type="primary" htmlType="submit" style={{ backgroundColor: "coral", color: "white" }}>
                                            Login
                                        </Button>
                                    </Form.Item>
                                </Form>
                                <div>Don't have an account? <Link to="/register" style={{ color: "coral" }} >Register here</Link></div>
                            </div>
                        </div>
                    </div>
                    <ToastContainer />
                </div>
            </div>
        </div>
    )
}

export default Login;