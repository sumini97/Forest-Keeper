import React, { useCallback, useEffect, useState } from "react";
import Bar from "../Bar";
import "../Home.css";
import Send from "../../../../config/Send";
import { connect } from "react-redux";
import back from "../../../../res/img/back.png";

function TeamDetail({ userSlice }) {
  const [isParticipated, setIsParticipated] = useState(false);
  const [detail, setDetail] = useState({
    message: "",
    statusCode: "",
    id: "",
    nickname: "",
    title: "",
    createdAt: "",
    ploggingDate: "",
    total: 0,
    participants: [],
    mountainName: "",
    mountainCode: "",
    content: "",
    views: 0,
    close: false,
  });

  const matchingId = window.localStorage.getItem("matchingId");
  useEffect(() => {
    getArticle();
  }, []);
  useEffect(() => {
    confirmParticipation();
  }, [detail]);

  const btnStyle = {
    width: "25vw",
    height: "5vh",
    background: "#37CD76",
    color: "white",
    borderRadius: 15,
    border: "none",
    marginLeft: "29vw",
  };
  const clickdeBtnStyle = {
    width: "25vw",
    height: "5vh",
    background: "#FF7760",
    color: "white",
    borderRadius: 15,
    border: "none",
    marginLeft: "29vw",
  };

  function closeMatching() {
    Send.patch(`/match/close`, { matchingId: matchingId })
      .then((res) => {
        console.log(res);
        getArticle();
      })
      .catch((e) => {
        console.log(e);
      });
  }
  function getArticle() {
    Send.get(`/match/${matchingId}`)
      .then((res) => {
        console.log(res);
        setDetail(res.data);
        confirmParticipation();
      })
      .catch((e) => {
        console.log(e);
      });
  }

  function confirmParticipation() {
    if (isParticipated == false) {
      for (let i = 0; i < detail.participants.length; i++) {
        if (detail.participants[i].nickname == userSlice.userNickname) {
          setIsParticipated(true);
        }
      }
    }
  }

  function apply() {
    const data = {
      matchingId: matchingId,
    };
    Send.post(`/match/join`, data)
      .then((res) => {
        console.log(res);
        getArticle();
      })
      .catch((e) => {
        console.log(e);
      });
  }

  function cancel() {
    Send.delete(`/match/cancel/${matchingId}`)
      .then((res) => {
        console.log(res);
        setIsParticipated(false);
        getArticle();
      })
      .catch((e) => {
        console.log(e);
      });
  }

  function deleteMatching() {
    Send.delete(`/match/${matchingId}`)
      .then((res) => {
        console.log(res);
        alert("?????????????????????.");
        document.location.href = `/detail/${detail.mountainCode}`;
      })
      .catch((e) => {
        console.log(e);
      });
  }
  return (
    <>
      {/* 92.5vh */}
      <div
        style={{ height: "10.5vh" }}
        onClick={() =>
          (document.location.href = `/teamList/${detail.mountainCode}`)
        }
      >
        <img
          style={{
            marginTop: "5vh",
            marginLeft: "8.3vw",
            width: "2.5vh",
            height: "2.5vh",
          }}
          src={back}
        />
      </div>

      <div
        style={{
          marginLeft: "8.3vw",
          width: "83.3vw",
          marginRight: "8.3vw",
          height: "82vh",
        }}
      >
        <div>
          <div style={{ fontSize: "2.2vh", marginLeft: "2vw" }}>
            <b>{detail.nickname}</b>
          </div>
          <div
            style={{
              color: "#ACACAC",
              fontSize: "2vh",
              marginLeft: "2vw",
              marginBottom: "3vh",
            }}
          >
            {detail.createdAt.substr(0, 10) +
              " " +
              detail.createdAt.substr(11, 8)}
          </div>
        </div>

        <div
          id="banner"
          style={{
            width: "78.3vw",
            height: "15vh",
            background: "#EAF9E6",
            borderRadius: 10,
            marginBottom: "4.8vh",
            color: "#8ABC9A",
            paddingTop: "2.1vh",
            paddingLeft: "5vw",
            fontSize: "2vh",
          }}
        >
          <div style={{ display: "flex", marginBottom: "1.8vh" }}>
            <div style={{ marginRight: "2vw", marginTop: "0.3vh" }}>
              ???????????? ??? :{" "}
            </div>
            <div style={{ fontSize: "2.3vh" }}>
              <b>{detail.mountainName}</b>{" "}
            </div>
          </div>
          <div style={{ display: "flex", marginBottom: "1.8vh" }}>
            <div style={{ marginRight: "2vw", marginTop: "0.3vh" }}>
              ?????? :{" "}
            </div>
            <div style={{ fontSize: "2.3vh" }}>
              <b>{detail.ploggingDate}</b>{" "}
            </div>
          </div>
          <div style={{ display: "flex" }}>
            <div style={{ marginRight: "2vw", marginTop: "0.3vh" }}>
              ???????????? :{" "}
            </div>
            <div style={{ fontSize: "2.3vh" }}>
              <b>
                {detail.participants.length}/{detail.total} ???
              </b>
            </div>
          </div>
        </div>
        <div
          style={{
            fontSize: "2.2vh",
            color: "#515153",
            marginBottom: "1.2vh",
            marginLeft: "2vw",
          }}
        >
          <b>{detail.title}</b>
        </div>
        <div style={{ marginLeft: "2vw", marginBottom: "7vh" }}>
          {detail.content}
        </div>

        {detail.close == true ? (
          <>
            <button style={clickdeBtnStyle}>?????? ??????</button>
            <div style={{ height: "1vh" }}></div>
            <button style={clickdeBtnStyle} onClick={() => deleteMatching()}>
              ????????????
            </button>
          </>
        ) : userSlice.userNickname == detail.nickname ? (
          <>
            <button style={btnStyle} onClick={() => closeMatching()}>
              ????????????
            </button>
            <div style={{ height: "1vh" }}></div>
            <button style={clickdeBtnStyle} onClick={() => deleteMatching()}>
              ????????????
            </button>
          </>
        ) : isParticipated == false ? (
          <button style={btnStyle} onClick={() => apply()}>
            ????????????
          </button>
        ) : (
          <button style={clickdeBtnStyle} onClick={() => cancel()}>
            ????????????
          </button>
        )}
      </div>
      <Bar></Bar>
    </>
  );
}
function mapStateToProps(state) {
  return { userSlice: state.user };
}

export default connect(mapStateToProps)(TeamDetail);
