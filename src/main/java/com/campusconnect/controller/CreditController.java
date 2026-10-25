package com.campusconnect.controller;

import com.campusconnect.config.AppConfig;
import com.campusconnect.model.SkillCreditTransaction;
import com.campusconnect.model.User;
import com.campusconnect.service.CreditLedgerService;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import java.io.IOException;
import java.util.List;

@WebServlet("/credits")
public class CreditController extends HttpServlet {

    private final CreditLedgerService creditService = new CreditLedgerService();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        HttpSession session = req.getSession(false);
        User currentUser = (session != null) ? (User) session.getAttribute("currentUser") : null;
        if (currentUser == null) {
            resp.sendRedirect(req.getContextPath() + "/auth/login");
            return;
        }

        try {
            int uid = currentUser.getId();
            int balance = creditService.getBalance(uid);
            int totalEarned = creditService.getTotalEarned(uid);
            int totalSpent = creditService.getTotalSpent(uid);
            List<SkillCreditTransaction> history = creditService.getLedgerHistory(uid, 50, 0);

            req.setAttribute("balance", balance);
            req.setAttribute("totalEarned", totalEarned);
            req.setAttribute("totalSpent", totalSpent);
            req.setAttribute("history", history);

            // Policy attributes
            req.setAttribute("sessionCost", AppConfig.getSessionBookingCost());
            req.setAttribute("sessionReward", AppConfig.getSessionCompletionReward());
            req.setAttribute("resourceReward", AppConfig.getResourceUploadReward());
            req.setAttribute("answerReward", AppConfig.getAcceptedAnswerReward());
        } catch (Exception e) {
            req.setAttribute("errorMessage", "Error loading credit ledger: " + e.getMessage());
        }

        req.getRequestDispatcher("/WEB-INF/views/credits/index.jsp").forward(req, resp);
    }
}