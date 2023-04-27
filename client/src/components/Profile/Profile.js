import React from "react";
import { useSelector } from "react-redux";
import { ToastContainer } from "react-toastify";
import { Avatar } from 'antd'
import { UserOutlined } from '@ant-design/icons';

const Profile = () => {
    const { profile } = useSelector(state => state.user)

    return (
        <div className="d-flex flex-column flex-wrap align-items-center">

            <h2 className="mb-5">Profile Information</h2>
            <Avatar
                className="d-flex justify-content-center align-items-center bg-dark mb-5"
                size={200}
                icon={<UserOutlined />}
            />

            <div className="card bg-glass w-50 mb-5">
                <h4 className="card-header bg-gradient text-white" style={{background: "coral"}}> General Information</h4>
                <div className="card-body">

                            <div className="d-flex flex-column">
                                <div><b>Username:</b> {profile.username}</div>
                                    <>
                                        <div><b>First name:</b> {profile.firstName}</div>
                                        <div><b>Last name:</b> {profile.lastName}</div>
                                    </>
                            </div>
                </div>
            </div>

            <div className="card bg-glass w-50 mb-5">
                <h4 className="card-header bg-gradient text-white" style={{background: "coral"}}>Contact Details</h4>
                <div className="card-body">
                            <div className="d-flex flex-column">
                                <div><b>Email:</b> {profile.email}</div>
                                <div><b>Phone:</b> {profile.phone}</div>
                            </div>
                </div>
            </div>

            <div className="card bg-glass w-50 mb-5">
                <h4 className="card-header  bg-gradient text-white" style={{background: "coral"}}>Address</h4>
                <div className="card-body">
                        <div className="card card-body mt-3 mb-3">
                            {profile.address && <><div>{profile.address.address1}, {profile['address'].address2}</div>
                            <div>{profile.address.city}</div>
                            <div>{profile.address.state} - {profile['address'].zipcode}</div></>}
                        </div>
            </div>
            </div>
            <ToastContainer />
        </div >

    );
}

export default Profile;