import { useState } from "react";
import "./index.scss";
import { MainPage } from "../main-page";
import { DemandChatPage } from "../demand-chat-page";
import { EnergyChatPage } from "../energy-chat-page";
import { PowerTradingPage } from "../power-trading-page";
import { ProfitForecastPage } from "../profit-forecast-page";
import { ProfitManagePage } from "../profit-manage-page";
import "../../../working/Energy/index.scss";
export const AssistMode = (props) => {
  return (
    <>
      {props.isDemandPage ? (
        <DemandChatPage setIsDemandPage={props.setIsDemandPage} />
      ) : (
        <>
          {props.isEnergyPage ? (
            <EnergyChatPage setIsEnergyPage={props.setIsEnergyPage} />
          ) : (
            <>
              {props.isTradePage ? (
                <PowerTradingPage setIsTradePage={props.setIsTradePage} />
              ) : (
                <>
                  {props.isProfitPage ? (
                    <ProfitForecastPage
                      setIsProfitPage={props.setIsProfitPage}
                      isExit={props.isExit}
                    />
                  ) : (
                    <>
                      {props.isProfitManagePage ? (
                        <ProfitManagePage
                          setIsProfitManagePage={props.setIsProfitManagePage}
                        />
                      ) : (
                        <MainPage
                          {...props}
                          setIsDemandPage={props.setIsDemandPage}
                          setIsEnergyPage={props.setIsEnergyPage}
                          setIsTradePage={props.setIsTradePage}
                          setIsProfitPage={props.setIsProfitPage}
                          setIsProfitManPage={props.setIsProfitManagePage}
                        />
                      )}
                    </>
                  )}
                </>
              )}
            </>
          )}
        </>
      )}
    </>
  );
};
